package com.mmk.kmpnotifier.firebase

import cocoapods.FirebaseMessaging.FIRMessaging
import cocoapods.FirebaseMessaging.FIRMessagingDelegateProtocol
import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.PushNotifier
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.registerForRemoteNotifications
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


internal class FirebasePushNotifierImpl : PushNotifier {

    @OptIn(ExperimentalForeignApi::class)
    override fun doAfterInitialization() {
        UIApplication.sharedApplication.registerForRemoteNotifications()
        FIRMessaging.messaging().delegate = FirebaseMessageDelegate()
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getToken(): String? = suspendCoroutine { cont ->

        FIRMessaging.messaging().tokenWithCompletion { token, error ->
            cont.resume(token)
            error?.let { println("Error while getting token: $error") }
        }

    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun deleteMyToken() = suspendCoroutine { cont ->
        FIRMessaging.messaging().deleteTokenWithCompletion {
            cont.resume(Unit)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun subscribeToTopic(topic: String) {
        FIRMessaging.messaging().subscribeToTopic(topic)
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun unSubscribeFromTopic(topic: String) {
        FIRMessaging.messaging().unsubscribeFromTopic(topic)
    }


    @OptIn(ExperimentalForeignApi::class)
    private class FirebaseMessageDelegate : FIRMessagingDelegateProtocol, NSObject() {
        private val notifierFactory by lazy { NotifierManagerImpl }
        override fun messaging(messaging: FIRMessaging, didReceiveRegistrationToken: String?) {
            didReceiveRegistrationToken?.let { token ->
                println("FirebaseMessaging: onNewToken is called")
                notifierFactory.onNewToken(didReceiveRegistrationToken)
            }
        }


    }
}