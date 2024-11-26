package com.mmk.kmpnotifier.notification

import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mmk.kmpnotifier.notification.configuration.DisplayNotificationManager

/**
 * Default implementation of the Android notification manager
 */
internal class DefaultDisplayNotificationManager : DisplayNotificationManager, AndroidNotificationManager {
    private var notificationData: AndroidNotificationData? = null

    override fun setData(notificationData: AndroidNotificationData) {
        this.notificationData = notificationData
    }

    override fun displayNotification(title: String, body: String) {
        val data = notificationData ?: throw IllegalStateException("Android Notification data must be provided")
        val notificationChannelData =
            data.notificationChannelData ?: throw IllegalStateException("Notification Channel Data must be provided")

        val notification = NotificationCompat.Builder(
            data.context,
            notificationChannelData.id
        ).apply {
            setChannelId(notificationChannelData.id)
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(data.notificationIconResId)
            setAutoCancel(true)
            setContentIntent(data.pendingIntent)
            data.notificationIconColorResId?.let {
                color = ContextCompat.getColor(data.context, it)
            }
        }.build()

        data.notificationManager.notify(data.notificationId, notification)
    }
}
