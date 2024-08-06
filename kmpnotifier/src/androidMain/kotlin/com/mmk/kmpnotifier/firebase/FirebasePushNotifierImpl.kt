package com.mmk.kmpnotifier.firebase

import com.google.android.gms.common.internal.Preconditions
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.mmk.kmpnotifier.notification.DEFAULT_APP_NAME
import com.mmk.kmpnotifier.notification.PushNotifier
import kotlinx.coroutines.tasks.asDeferred

internal class FirebasePushNotifierImpl(private val appName: String) : PushNotifier {

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
        if (appName == DEFAULT_APP_NAME) return FirebaseMessaging.getInstance()
        else {
            val firebaseApp = FirebaseApp.getInstance(appName)
            val firebaseMessaging = firebaseApp.get(FirebaseMessaging::class.java)
            Preconditions.checkNotNull(firebaseMessaging, "Firebase Messaging component is not present")
            return firebaseMessaging
        }
    }


}