package com.mmk.kmpnotifier.permission

import com.prinum.utils.logger.Logger
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

internal class IosPermissionUtil(private val notificationCenter: UNUserNotificationCenter) :
    PermissionUtil {
    companion object {
        val TAG = "IosPermissionUtil"
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
        Logger.d(TAG, "Invoking askNotificationPermission")
        notificationCenter.requestAuthorizationWithOptions(NOTIFICATION_PERMISSIONS) { isGranted, _ ->
            Logger.d(TAG, "askNotificationPermission is granted: $isGranted")
            onPermissionResult(isGranted)
        }
    }
}