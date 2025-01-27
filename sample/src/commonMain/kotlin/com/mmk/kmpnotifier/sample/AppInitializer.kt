package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.PushNotifier


object AppInitializer {
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                println("Push Notification onNewToken: $token")
            }

            override fun onPushNotification(title: String?, body: String?, data: PayloadData) {
                println(
                    "Push Notification notification type message is received: Title: $title and Body: $body and data: $data"
                )
            }

            override fun onPayloadData(data: PayloadData) {
                println("Push Notification payloadData: $data")
            }

            override fun onNotificationClicked(data: PayloadData) {
                println("Notification clicked, Notification payloadData: $data")
            }
        })
    }
}