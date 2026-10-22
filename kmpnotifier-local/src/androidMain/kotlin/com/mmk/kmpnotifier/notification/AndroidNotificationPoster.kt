@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
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
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.ACTION_NOTIFICATION_ACTION
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_ACTION_ID
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_NOTIFICATION_ID
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_REMOTE_INPUT
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.coroutines.cancellation.CancellationException

/**
 * Builds and posts a local notification. Shared by [AndroidNotifier] (immediate notifications)
 * and [NotificationReceiver] (scheduled notifications, possibly in a cold process).
 */
internal class AndroidNotificationPoster(
    private val context: Context,
    private val androidNotificationConfiguration: NotificationPlatformConfiguration.Android,
    private val notificationChannelFactory: NotificationChannelFactory = NotificationChannelFactory(
        context = context,
        channelData = androidNotificationConfiguration.notificationChannelData
    ),
) {

    suspend fun post(builder: NotifierBuilder) {
        val notificationManager = context.notificationManager ?: return
        notificationChannelFactory.createChannels()
        val pendingIntent = getPendingIntent(builder.payloadData, builder.id)
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
            setOnlyAlertOnce(true)
            setContentIntent(pendingIntent)
            androidNotificationConfiguration.notificationIconColorResId?.let {
                color = ContextCompat.getColor(context, it)
            }

            // Add action buttons
            builder.actions.forEach { action ->
                val actionPendingIntent =
                    createActionPendingIntent(
                        actionId = action.id,
                        notificationId = builder.id,
                        payload = builder.payloadData
                    )

                val actionBuilder = NotificationCompat.Action.Builder(
                    0,
                    action.title,
                    actionPendingIntent
                )

                if (action.allowsTextInput) {
                    val remoteInput = androidx.core.app.RemoteInput.Builder(EXTRA_REMOTE_INPUT)
                        .setLabel(action.inputLabel ?: "Enter value")
                        .build()

                    actionBuilder.addRemoteInput(remoteInput)
                }

                addAction(actionBuilder.build())
            }

        }.build()
        notificationManager.notify(builder.id, notification)
    }

    private fun getPendingIntent(payloadData: Map<String, String>, id: Int): PendingIntent? {
        val intent = getLauncherActivityIntent()?.apply {
            putExtra(ACTION_NOTIFICATION_CLICK, ACTION_NOTIFICATION_CLICK)
            payloadData.forEach { putExtra(it.key, it.value) }
            val urlData = payloadData.getOrDefault(Notifier.KEY_URL, null)
            urlData?.let { setData(Uri.parse(urlData)) }
        }
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT


        return PendingIntent.getActivity(context, id, intent, flags)
    }

    private fun getLauncherActivityIntent(): Intent? {
        val packageManager = context.applicationContext.packageManager
        return packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)
    }

    private fun createActionPendingIntent(
        actionId: String,
        notificationId: Int,
        payload: Map<String, String>
    ): PendingIntent {

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_NOTIFICATION_ACTION
            putExtra(EXTRA_ACTION_ID, actionId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            payload.forEach { putExtra(it.key, it.value) }
        }


        val flags = PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE
        return PendingIntent.getBroadcast(context, (actionId + notificationId).hashCode(), intent, flags)
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
