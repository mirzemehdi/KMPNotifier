package com.mmk.kmpnotifier.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.mmk.kmpnotifier.notification.PushNotifier
import kotlinx.coroutines.tasks.asDeferred
import kotlin.coroutines.cancellation.CancellationException

internal class FirebasePushNotifierImpl : PushNotifier {

    init {
        println("FirebasePushNotifier is initialized")
    }

    override suspend fun getToken(): String? {
        return try {
            return FirebaseMessaging.getInstance().token.asDeferred().await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null.also {
                println("Error while getting token: $e")
            }
        }
    }

    override suspend fun deleteMyToken() {
        FirebaseMessaging.getInstance().deleteToken()
    }

    override suspend fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }


}