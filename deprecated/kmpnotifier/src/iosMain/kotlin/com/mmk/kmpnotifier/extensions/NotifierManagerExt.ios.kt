@file:Suppress("UnusedReceiverParameter", "DEPRECATION")
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
@Deprecated(
    message = "Use KMPNotifier.onApplicationDidReceiveRemoteNotification(userInfo) from " +
            "kmpnotifier-push-firebase. See MIGRATION.md.",
    replaceWith = ReplaceWith(
        "KMPNotifier.onApplicationDidReceiveRemoteNotification(userInfo)",
        "com.mmk.kmpnotifier.KMPNotifier",
        "com.mmk.kmpnotifier.push.firebase.onApplicationDidReceiveRemoteNotification",
    ),
)
public fun NotifierManager.onApplicationDidReceiveRemoteNotification(userInfo: Map<Any?, *>) {
    KMPNotifier.onApplicationDidReceiveRemoteNotification(userInfo)
}

@Deprecated(
    message = "Handled automatically by the notification delegate; if called manually, use " +
            "KMPNotifier.onUserNotification(notificationContent). See MIGRATION.md.",
)
public fun NotifierManager.onUserNotification(notificationContent: UNNotificationContent) {
    emitPushEventsIfRemote(notificationContent)
}

@Deprecated(
    message = "Use KMPNotifier.onNotificationClicked(notificationContent). See MIGRATION.md.",
    replaceWith = ReplaceWith(
        "KMPNotifier.onNotificationClicked(notificationContent)",
        "com.mmk.kmpnotifier.KMPNotifier",
        "com.mmk.kmpnotifier.extensions.onNotificationClicked",
    ),
)
public fun NotifierManager.onNotificationClicked(notificationContent: UNNotificationContent) {
    KMPNotifier.onNotificationClicked(notificationContent)
}
