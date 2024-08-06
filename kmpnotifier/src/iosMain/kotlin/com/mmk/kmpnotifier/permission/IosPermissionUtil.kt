package com.mmk.kmpnotifier.permission

import com.mmk.kmpnotifier.notification.IosNotifier
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

internal class IosPermissionUtil(private val notificationCenter: UNUserNotificationCenter) :
    PermissionUtil {
    companion object {
        val NOTIFICATION_PERMISSIONS =
            UNAuthorizationOptionAlert or
                    UNAuthorizationOptionSound or
                    UNAuthorizationOptionBadge
    }

    override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        notificationCenter.getNotificationSettingsWithCompletionHandler {
            onPermissionResult(it?.authorizationStatus == UNAuthorizationStatusAuthorized)
        }
    }

    override fun askNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        notificationCenter.requestAuthorizationWithOptions(NOTIFICATION_PERMISSIONS) { isGranted, _ ->
            if (isGranted) {
                UNUserNotificationCenter.currentNotificationCenter().delegate =
                    IosNotifier.NotificationDelegate()
            }
            onPermissionResult(isGranted)
        }
    }
}