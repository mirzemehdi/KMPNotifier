@file:Suppress("UnusedReceiverParameter")
@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.extensions

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import platform.UserNotifications.UNNotificationContent

/**
 * Emits the notification-clicked event for the given notification content.
 */
public fun KMPNotifier.onNotificationClicked(notificationContent: UNNotificationContent) {
    NotifierEventHub.emitNotificationClicked(notificationContent.userInfo.asPayloadData())
}
