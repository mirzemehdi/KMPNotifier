package com.mmk.kmpnotifier.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mmk.kmpnotifier.Constants.ACTION_NOTIFICATION_CLICK
import com.mmk.kmpnotifier.extensions.notificationManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random


internal class AndroidNotifier(
    private val context: Context,
    private val androidNotificationConfiguration: NotificationPlatformConfiguration.Android,
    private val notificationChannelFactory: NotificationChannelFactory,
    private val permissionUtil: PermissionUtil,
) : Notifier {

    private val scope by lazy { MainScope() }

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify {
            this.id = notificationID
            this.title = title
            this.body = body
            this.payloadData = payloadData
        }
        return notificationID
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        notify {
            this.id = id
            this.title = title
            this.body = body
            this.payloadData = payloadData
        }
    }

    override fun notify(block: NotifierBuilder.() -> Unit) {
        val builder = NotifierBuilder().apply(block)
        permissionUtil.hasNotificationPermission {
            if (it.not())
                Log.w(
                    "AndroidNotifier", "You need to ask runtime " +
                            "notification permission (Manifest.permission.POST_NOTIFICATIONS) in your activity"
                )
        }
        val notificationManager = context.notificationManager ?: return
        val pendingIntent = getPendingIntent(builder.payloadData)
        notificationChannelFactory.createChannels()
        scope.launch {
            val imageBitmap = builder.image?.asBitmap()
            val notification = NotificationCompat.Builder(
                context,
                androidNotificationConfiguration.notificationChannelData.id
            ).apply {
                setChannelId(androidNotificationConfiguration.notificationChannelData.id)
                setContentTitle(builder.title)
                setContentText(builder.body)
                imageBitmap?.let {
                    setLargeIcon(it)
                    setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(it)
                            .bigLargeIcon(null as Bitmap?)
                    )
                }
                setSmallIcon(androidNotificationConfiguration.notificationIconResId)
                setAutoCancel(true)
                setContentIntent(pendingIntent)
                androidNotificationConfiguration.notificationIconColorResId?.let {
                    color = ContextCompat.getColor(context, it)
                }
            }.build()
            notificationManager.notify(builder.id, notification)
        }


    }

    override fun remove(id: Int) {
        val notificationManager = context.notificationManager ?: return
        notificationManager.cancel(id)
    }

    override fun removeAll() {
        val notificationManager = context.notificationManager ?: return
        notificationManager.cancelAll()
    }

    private fun getPendingIntent(payloadData: Map<String, String>): PendingIntent? {
        val intent = getLauncherActivityIntent()?.apply {
            putExtra(ACTION_NOTIFICATION_CLICK, ACTION_NOTIFICATION_CLICK)
            payloadData.forEach { putExtra(it.key, it.value) }
            val urlData = payloadData.getOrDefault(Notifier.KEY_URL, null)
            urlData?.let { setData(Uri.parse(urlData)) }
        }
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT


        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    private fun getLauncherActivityIntent(): Intent? {
        val packageManager = context.applicationContext.packageManager
        return packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)
    }

    private suspend fun NotificationImage?.asBitmap(): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                when (this@asBitmap) {
                    null -> null
                    is NotificationImage.Url -> {
                        URL(url).openStream().buffered().use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)
                        }
                    }

                    is NotificationImage.File -> {
                        BitmapFactory.decodeFile(path)
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(
                    "AndroidNotifier",
                    "Error while processing notification image. Ensure correct path or internet connection.",
                    e
                )
                null
            }
        }
    }


}