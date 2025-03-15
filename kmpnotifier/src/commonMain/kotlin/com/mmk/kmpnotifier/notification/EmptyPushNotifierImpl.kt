package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.logger.currentLogger


internal class EmptyPushNotifierImpl : PushNotifier {
    override suspend fun getToken(): String? {
        currentLogger.log("Not implemented: Get firebase token returns null")
        return null
    }

    override suspend fun deleteMyToken() {
        currentLogger.log("Not implemented: Delete firebase token is called")
    }

    override suspend fun subscribeToTopic(topic: String) {
        currentLogger.log("Not implemented: Subscribe firebase topic is called")
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        currentLogger.log("Not implemented: Unsubscribe firebase topic is called")
    }
}
