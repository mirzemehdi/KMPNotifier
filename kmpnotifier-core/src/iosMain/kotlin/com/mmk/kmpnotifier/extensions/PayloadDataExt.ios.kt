@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.extensions

import com.mmk.kmpnotifier.Constants.KEY_IOS_FIREBASE_NOTIFICATION
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.notification.PayloadData
import platform.UserNotifications.UNNotificationContent

@InternalKMPNotifierApi
public fun Map<Any?, *>?.asPayloadData(): PayloadData {
    return this.orEmpty().let { data ->
        data.keys
            .filterNotNull()
            .filterIsInstance<String>()
            .associateWith { key -> data[key] }
    }
}

@InternalKMPNotifierApi
public fun UNNotificationContent.isRemoteNotification(): Boolean {
    return userInfo.containsKey(KEY_IOS_FIREBASE_NOTIFICATION)
}

/**
 * Emits push events into [NotifierEventHub] when the given notification content
 * originates from a remote (Firebase) push message. No-op for local notifications.
 *
 * Shared by the local notification delegate, the push module's extension functions
 * and the deprecated `NotifierManager.onUserNotification`.
 */
@InternalKMPNotifierApi
public fun emitPushEventsIfRemote(notificationContent: UNNotificationContent) {
    val userInfo = notificationContent.userInfo
    val payloadData = userInfo.asPayloadData()
    val hasNotification = notificationContent.title != null || notificationContent.body != null
    if (notificationContent.isRemoteNotification() && hasNotification) {
        NotifierEventHub.emitPushNotification(
            title = notificationContent.title,
            body = notificationContent.body
        )
    }
    if (notificationContent.isRemoteNotification()) {
        NotifierEventHub.emitPushPayloadData(payloadData)
        NotifierEventHub.emitPushNotificationWithPayloadData(
            title = notificationContent.title,
            body = notificationContent.body,
            data = payloadData
        )
    }
}
