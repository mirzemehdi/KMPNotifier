package com.mmk.kmpnotifier.notification


internal class EmptyPushNotifierImpl : PushNotifier {
    override suspend fun getToken(): String? {
        println("Not implemented: Get firebase token returns null")
        return null
    }

    override suspend fun deleteMyToken() {
        println("Not implemented: Delete firebase token is called")
    }

    override suspend fun subscribeToTopic(topic: String) {
        println("Not implemented: Subscribe firebase topic is called")
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        println("Not implemented: Unsubscribe firebase topic is called")
    }
}
