package com.mmk.kmpnotifier.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mmk.kmpnotifier.Constants
import com.mmk.kmpnotifier.extensions.shouldShowNotification
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.NotifierManagerImpl

internal class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notifierManager by lazy { NotifierManagerImpl }
    private val notifier: Notifier by lazy { notifierManager.getLocalNotifier() }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("FirebaseMessaging: onNewToken is called")
        notifierManager.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val payloadData = message.data
        val notification = message.notification
        notification?.let {
            if (notifierManager.shouldShowNotification())
                notifier.notify(
                    title = notification.title ?: "",
                    body = notification.body ?: "",
                    payloadData = payloadData
                )

            notifierManager.onPushNotification(title = notification.title, body = notification.body)
        }
        val payloadDataWithClickAction = payloadData
            .takeIf { it.isNotEmpty() }
            ?.plus(Constants.ACTION_NOTIFICATION_CLICK to Constants.ACTION_NOTIFICATION_CLICK)
            ?: payloadData

        if (payloadDataWithClickAction.isNotEmpty()) {
            notifierManager.onPushPayloadData(payloadDataWithClickAction)
        }
        notifierManager.onPushNotificationWithPayloadData(
            title = notification?.title,
            body = notification?.body,
            data = payloadDataWithClickAction
        )
    }
}