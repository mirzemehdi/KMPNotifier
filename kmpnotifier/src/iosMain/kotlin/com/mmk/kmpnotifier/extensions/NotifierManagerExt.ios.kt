@file:Suppress("UnusedReceiverParameter")
@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.extensions

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.push.firebase.onApplicationDidReceiveRemoteNotification
import platform.UserNotifications.UNNotificationContent

/***
 * In order to receive notification data payload this functions needs to be called in
 * ios Swift side application didReceiveRemoteNotification function
 *
 * Example:
 *
 * ```
 * func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any]) async -> UIBackgroundFetchResult {
 *   NotifierManager.shared.onApplicationDidReceiveRemoteNotification(userInfo: userInfo)
 *   return UIBackgroundFetchResult.newData
 * }
 * ```
 */
public fun NotifierManager.onApplicationDidReceiveRemoteNotification(userInfo: Map<Any?, *>) {
    KMPNotifier.onApplicationDidReceiveRemoteNotification(userInfo)
}

public fun NotifierManager.onUserNotification(notificationContent: UNNotificationContent) {
    emitPushEventsIfRemote(notificationContent)
}

public fun NotifierManager.onNotificationClicked(notificationContent: UNNotificationContent) {
    KMPNotifier.onNotificationClicked(notificationContent)
}
