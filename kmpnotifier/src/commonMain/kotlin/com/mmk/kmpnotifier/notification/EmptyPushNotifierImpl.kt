package com.mmk.kmpnotifier.notification


internal class EmptyPushNotifierImpl : PushNotifier() {
    override suspend fun getToken(): String? = callSafe {
        error("Not implemented: Get firebase token returns null")
    }.getOrNull()

    override suspend fun deleteMyToken() = callSafe {
        error("Not implemented: Delete firebase token is called")
    }.isSuccess

    override suspend fun subscribeToTopic(topic: String) = callSafe {
        error("Not implemented: Subscribe firebase topic is called")
    }.isSuccess

    override suspend fun unSubscribeFromTopic(topic: String) = callSafe {
        error("Not implemented: Unsubscribe firebase topic is called")
    }.isSuccess
}
