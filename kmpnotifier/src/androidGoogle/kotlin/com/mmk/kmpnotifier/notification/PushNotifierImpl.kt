package com.mmk.kmpnotifier.notification

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.logger.log


internal class PushNotifierImpl : PushNotifier() {

    init {
        currentLogger.log("PushNotifier is initialized")
    }

    private val messaging by lazy { FirebaseMessaging.getInstance() }

    override suspend fun getToken(): String? = messaging.token.await(
        onFailure = { currentLogger.log("Error while getting token: $it") }
    ).getOrNull()

    override suspend fun deleteMyToken(): Boolean = callSafe(
        onFailure = { currentLogger.log("Error while deleting token: $it") },
        block = { messaging.deleteToken() }
    ).isSuccess

    override suspend fun subscribeToTopic(topic: String): Boolean = callSafe(
        onFailure = { currentLogger.log("Error while subscribing to topic $topic: $it") },
        block = { messaging.subscribeToTopic(topic) }
    ).isSuccess

    override suspend fun unSubscribeFromTopic(topic: String): Boolean = callSafe(
        onFailure = { currentLogger.log("Error while unsubscribing from topic $topic: $it") },
        block = { messaging.unsubscribeFromTopic(topic) }
    ).isSuccess

    private fun <T> Task<T>.await(onFailure: (Throwable) -> Unit = currentLogger::log) =
        callSafe(onFailure = onFailure) { Tasks.await<T>(this) }
}
