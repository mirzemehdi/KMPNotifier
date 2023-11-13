package com.mmk.kmpnotifier.firebase

import com.mmk.kmpnotifier.notification.PushNotifier

internal class FirebasePushNotifierImpl:PushNotifier {
    override suspend fun getToken(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMyToken() {
        TODO("Not yet implemented")
    }

    override suspend fun subscribeToTopic(topic: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        TODO("Not yet implemented")
    }
}