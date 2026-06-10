package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.local.localNotifier
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.push.PushListener
import com.mmk.kmpnotifier.push.firebase.addPushListener


object AppInitializer {
    fun onApplicationStart() {
        println("Application is started")
        onApplicationStartPlatformSpecific()
        KMPNotifier.setLogger { message ->
            println("KMPNotifier: $message")
        }

        KMPNotifier.addListener(object : KMPNotifier.Listener {
            override fun onAction(actionId: String, notificationId: Int, payload: Map<String, Any?>) {
                val actionMessage = "Action '$actionId' triggered on notification '$notificationId' with payload: $payload"
                println("Action triggered: $actionMessage")
                val localNotifier = KMPNotifier.localNotifier

                if (actionId == "com.mmk.kmpnotifier.EVENT_SCHEDULED_NOTIFICATION_FIRED_INTERNAL") {
                    localNotifier.notify {
                        id = notificationId
                        title = "Showing scheduled notification"
                        body = "Notification triggered on notification '$notificationId' with payload: $payload"
                    }
                }
                if (actionId == "CUSTOM_SNOOZE") {
                    localNotifier.removeAll()
                }
            }

            override fun onNotificationClicked(data: PayloadData) {
                println("Notification clicked, Notification payloadData: $data")
            }
        })

        // Push events come from the push module; on desktop/web the notifier is a no-op,
        // so the same listener code works on every target.
        KMPNotifier.addPushListener(object : PushListener {
            override fun onNewToken(token: String) {
                println("Push Notification onNewToken: $token")
            }

            override fun onPushNotification(title: String?, body: String?) {
                println("Push Notification notification type message is received: Title: $title and Body: $body")
            }

            override fun onPayloadData(data: PayloadData) {
                println("Push Notification payloadData: $data")
            }

            override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
                println("Push Notification is received: Title: $title and Body: $body and Notification payloadData: $data")
            }
        })
    }
}
