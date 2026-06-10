@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.firebase

import swiftPMImport.io.github.mirzemehdi.kmpnotifier.push.firebase.FIRMessaging
import swiftPMImport.io.github.mirzemehdi.kmpnotifier.push.firebase.FIRMessagingDelegateProtocol
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.PushNotifier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalForeignApi::class)
internal class FirebasePushNotifierImpl : PushNotifier {

    private val firebaseMessageDelegate by lazy { FirebaseMessageDelegate() }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        scope.launch {
            currentLogger.log("FirebasePushNotifier is initializing")
            FIRMessaging.messaging().delegate = firebaseMessageDelegate
            UIApplication.sharedApplication.registerForRemoteNotifications()
        }
    }

    override suspend fun getToken(): String? = suspendCoroutine { cont ->
        FIRMessaging.messaging().tokenWithCompletion { token, error ->
            cont.resume(token)
            error?.let { currentLogger.log("Error while getting token: $error") }
        }

    }

    override suspend fun deleteMyToken() = suspendCoroutine { cont ->
        FIRMessaging.messaging().deleteTokenWithCompletion {
            cont.resume(Unit)
        }
    }

    override suspend fun subscribeToTopic(topic: String) {
        FIRMessaging.messaging().subscribeToTopic(topic)
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        FIRMessaging.messaging().unsubscribeFromTopic(topic)
    }


    private class FirebaseMessageDelegate : FIRMessagingDelegateProtocol, NSObject() {
        override fun messaging(messaging: FIRMessaging, didReceiveRegistrationToken: String?) {
            didReceiveRegistrationToken?.let { token ->
                currentLogger.log("FirebaseMessaging: onNewToken is called")
                NotifierEventHub.emitNewToken(token)
            }
        }

    }
}
