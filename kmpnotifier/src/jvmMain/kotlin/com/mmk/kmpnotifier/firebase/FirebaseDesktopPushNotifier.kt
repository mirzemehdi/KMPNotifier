package com.mmk.kmpnotifier.firebase

import com.mmk.kmpnotifier.notification.PushNotifier

internal class FirebaseDesktopPushNotifier:PushNotifier {
    override suspend fun getToken(): String? {
        println("Get firebase toekn")
        return null
    }

    override suspend fun deleteMyToken() {
        println("Delete firebase toekn")
    }

    override suspend fun subscribeToTopic(topic: String) {
        println("Subscribe firebase topic")
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        println("Unsubscribe firebase topic")
    }
}