package com.mmk.kmpnotifier.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.NotificationHandler
import com.mmk.kmpnotifier.notification.NotifierManagerImpl


internal class GoogleMessagingService : FirebaseMessagingService() {
    private val notifierManager by lazy { NotifierManagerImpl }
    private val notifier by lazy { notifierManager.getLocalNotifier() }
    private val handler by lazy { NotificationHandler(notifierManager, notifier) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        currentLogger.log("GoogleMessagingService: onNewToken is called")
        notifierManager.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        handler.handle(
            title = message.notification?.title,
            body = message.notification?.body,
            data = message.data
        )
    }
}
