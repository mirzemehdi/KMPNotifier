@file:Suppress("UnusedReceiverParameter")

package com.mmk.kmpnotifier.extensions

import com.mmk.kmpnotifier.Constants.KEY_IOS_FIREBASE_NOTIFICATION
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
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
    val payloadData = userInfo.asPayloadData()
    if (payloadData.containsKey(KEY_IOS_FIREBASE_NOTIFICATION)) {
        NotifierManagerImpl.onPushPayloadData(payloadData)
        NotifierManagerImpl.onPushNotificationWithPayloadData(data = payloadData)
    }
}

public fun NotifierManager.onUserNotification(notificationContent: UNNotificationContent) {
    val userInfo = notificationContent.userInfo
    val payloadData = userInfo.asPayloadData()
    val hasNotification = notificationContent.title != null || notificationContent.body != null
    if (notificationContent.isPushNotification() && hasNotification) {
        NotifierManagerImpl.onPushNotification(
            title = notificationContent.title,
            body = notificationContent.body
        )
    }
    if (notificationContent.isPushNotification()) {
        NotifierManagerImpl.onPushPayloadData(payloadData)
        NotifierManagerImpl.onPushNotificationWithPayloadData(
            title = notificationContent.title,
            body = notificationContent.body,
            data = payloadData
        )
    }
}

public fun NotifierManager.onNotificationClicked(notificationContent: UNNotificationContent) {
    NotifierManagerImpl.onNotificationClicked(notificationContent.userInfo.asPayloadData())
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

internal fun Map<Any?, *>?.asPayloadData(): PayloadData {
    return this.orEmpty().let { data ->
        data.keys
            .filterNotNull()
            .filterIsInstance<String>()
            .associateWith { key -> data[key] }
    }
}

private fun UNNotificationContent.isPushNotification(): Boolean {
    return userInfo.containsKey(KEY_IOS_FIREBASE_NOTIFICATION)
}
