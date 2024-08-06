package com.mmk.kmpnotifier.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.mmk.kmpnotifier.notification.PushNotifier
import kotlinx.coroutines.tasks.asDeferred

internal class FirebasePushNotifierImpl : PushNotifier {

    init {
        println("FirebasePushNotifier is initialized")
    }
    override suspend fun getToken(): String? {
        return getFirebaseMessagingInstance().token.asDeferred().await()
    }

    override suspend fun deleteMyToken() {
        getFirebaseMessagingInstance().deleteToken()
    }

    override suspend fun subscribeToTopic(topic: String) {
        getFirebaseMessagingInstance().subscribeToTopic(topic)
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        getFirebaseMessagingInstance().unsubscribeFromTopic(topic)
    }

    private fun getFirebaseMessagingInstance(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }


}