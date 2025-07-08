package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData


object AppInitializer {
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
        NotifierManager.setLogger { message ->
            println("KMPNotifier: $message")
        }
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                println("Push Notification onNewToken: $token")
            }

            override fun onPushNotification(title: String?, body: String?) {
                super.onPushNotification(title, body)
                println("Push Notification notification type message is received: Title: $title and Body: $body")
            }

            override fun onPayloadData(data: PayloadData) {
                super.onPayloadData(data)
                println("Push Notification payloadData: $data")
            }

            override fun onPushNotificationWithPayloadData(
                title: String?,
                body: String?,
                data: PayloadData
            ) {
                super.onPushNotificationWithPayloadData(title, body, data)
                println("Push Notification is received: Title: $title and Body: $body and Notification payloadData: $data")
            }

            override fun onNotificationClicked(data: PayloadData) {
                println("Notification clicked, Notification payloadData: $data")
            }
        })
    }
}
