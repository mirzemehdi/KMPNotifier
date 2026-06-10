@file:Suppress("UnusedReceiverParameter")
@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.push.firebase

import com.mmk.kmpnotifier.Constants.KEY_IOS_FIREBASE_NOTIFICATION
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.extensions.asPayloadData
import com.mmk.kmpnotifier.extensions.emitPushEventsIfRemote
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import platform.UserNotifications.UNNotificationContent

/***
 * In order to receive push notification data payload this function needs to be called in
 * iOS Swift side application didReceiveRemoteNotification function.
 *
 * Example:
 *
 * ```
 * func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) async -> UIBackgroundFetchResult {
 *   KMPNotifier.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
 *   return UIBackgroundFetchResult.newData
 * }
 * ```
 */
public fun KMPNotifier.onApplicationDidReceiveRemoteNotification(userInfo: Map<Any?, *>) {
    val payloadData = userInfo.asPayloadData()
    if (payloadData.containsKey(KEY_IOS_FIREBASE_NOTIFICATION)) {
        NotifierEventHub.emitPushPayloadData(payloadData)
        NotifierEventHub.emitPushNotificationWithPayloadData(data = payloadData)
    }
}

/**
 * Emits push events for the given notification content if it is a remote (Firebase) notification.
 * Usually not needed: the library's notification delegate handles this automatically.
 */
public fun KMPNotifier.onUserNotification(notificationContent: UNNotificationContent) {
    emitPushEventsIfRemote(notificationContent)
}
