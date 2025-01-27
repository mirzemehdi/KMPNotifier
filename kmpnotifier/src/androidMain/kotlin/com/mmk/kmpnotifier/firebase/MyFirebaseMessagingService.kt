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
        val payloadData = message.data
        val notification = message.notification
        if (notification != null) {
            notifierManager.onPushNotification(
                title = notification.title,
                body = notification.body,
                data = payloadData
            )
            if (notifierManager.shouldShowNotification()) {
                notifier.notify(
                    title = notification.title ?: "",
                    body = notification.body ?: "",
                    payloadData = payloadData
                )
            }
        } else if (payloadData.isNotEmpty()) {
            val data =
                payloadData + mapOf(Constants.ACTION_NOTIFICATION_CLICK to Constants.ACTION_NOTIFICATION_CLICK)
            notifierManager.onPushPayloadData(data)
        }
    }
}