package com.mmk.kmpnotifier.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mmk.kmpnotifier.Constants.ACTION_NOTIFICATION_CLICK
import com.mmk.kmpnotifier.extensions.notificationManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlin.random.Random


internal class AndroidNotifier(
    private val context: Context,
    private val androidNotificationConfiguration: NotificationPlatformConfiguration.Android,
    private val notificationChannelFactory: NotificationChannelFactory,
    private val permissionUtil: PermissionUtil,
) : Notifier {

    override fun notify(title: String, body: String, payloadData: Map<String, String>): Int {
        val notificationID = Random.nextInt(0, Int.MAX_VALUE)
        notify(notificationID, title, body, payloadData)
        return notificationID
    }

    override fun notify(id: Int, title: String, body: String, payloadData: Map<String, String>) {
        permissionUtil.hasNotificationPermission {
            if (it.not())
                Log.w(
                    "AndroidNotifier", "You need to ask runtime " +
                            "notification permission (Manifest.permission.POST_NOTIFICATIONS) in your activity"
                )
        }
        val notificationManager = context.notificationManager ?: return
        val pendingIntent = getPendingIntent(payloadData)
        notificationChannelFactory.createChannels()
        val notification = NotificationCompat.Builder(
            context,
            androidNotificationConfiguration.notificationChannelData.id
        ).apply {
            setChannelId(androidNotificationConfiguration.notificationChannelData.id)
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(androidNotificationConfiguration.notificationIconResId)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            androidNotificationConfiguration.notificationIconColorResId?.let {
                color = ContextCompat.getColor(context, it)
            }
        }.build()

        notificationManager.notify(id, notification)
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

}