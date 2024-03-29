package com.mmk.kmpnotifier.extensions

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import platform.UserNotifications.UNMutableNotificationContent
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
    val payloadData = userInfo.keys
        .filterNotNull()
        .filterIsInstance<String>()
        .associateWith { key -> userInfo[key] }

    if (payloadData.containsKey("gcm.message_id")) NotifierManagerImpl.onPushPayloadData(payloadData)
}

internal fun NotifierManager.onUserNotification(notificationContent: UNNotificationContent) {
    val userInfo = notificationContent.userInfo
    if (notificationContent.isPushNotification()) NotifierManagerImpl.onPushNotification(
        title = notificationContent.title,
        body = notificationContent.body
    )
    NotifierManager.onApplicationDidReceiveRemoteNotification(userInfo)
}

internal fun NotifierManager.shouldShowNotification(notificationContent: UNNotificationContent): Boolean {
    val configuration =
        NotifierManagerImpl.getConfiguration() as? NotificationPlatformConfiguration.Ios
    val configurationShowPushNotificationEnabled = configuration?.showPushNotification ?: true
    return when {
        notificationContent.isPushNotification() && !configurationShowPushNotificationEnabled -> false
        else -> true
    }
}

private fun UNNotificationContent.isPushNotification(): Boolean {
    return userInfo.containsKey("gcm.message_id")
}