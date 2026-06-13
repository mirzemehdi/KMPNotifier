@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mmk.kmpnotifier.Constants
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.local.LocalNotifications
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

internal class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notifier: Notifier by lazy { LocalNotifications.notifier }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        currentLogger.log("FirebaseMessaging: onNewToken is called")
        NotifierEventHub.emitNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val payloadData = message.data
        val notification = message.notification
        notification?.let {
            if (shouldShowNotification())
                notifier.notify(
                    title = notification.title ?: "",
                    body = notification.body ?: "",
                    payloadData = payloadData
                )

            NotifierEventHub.emitPushNotification(title = notification.title, body = notification.body)
        }
        val payloadDataWithClickAction = payloadData
            .takeIf { it.isNotEmpty() }
            ?.plus(Constants.ACTION_NOTIFICATION_CLICK to Constants.ACTION_NOTIFICATION_CLICK)
            ?: payloadData

        if (payloadDataWithClickAction.isNotEmpty()) {
            NotifierEventHub.emitPushPayloadData(payloadDataWithClickAction)
        }
        NotifierEventHub.emitPushNotificationWithPayloadData(
            title = notification?.title,
            body = notification?.body,
            data = payloadDataWithClickAction
        )
    }

    private fun shouldShowNotification(): Boolean {
        val configuration =
            NotifierInternals.configurationOrNull() as? NotificationPlatformConfiguration.Android
        return configuration?.showPushNotification ?: true
    }
}
