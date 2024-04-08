package com.mmk.kmpnotifier.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mmk.kmpnotifier.Constants
import com.mmk.kmpnotifier.extensions.shouldShowNotification
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

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
        message.notification?.let {
            if (notifierManager.shouldShowNotification())
                notifier.notify(it.title ?: "", it.body ?: "")

            notifierManager.onPushNotification(title = it.title, body = it.body)
        }
        if (message.data.isNotEmpty()) {
            val data = message.data + mapOf( Constants.ACTION_NOTIFICATION_CLICK to Constants.ACTION_NOTIFICATION_CLICK)
            notifierManager.onPushPayloadData(data)
        }
    }
}