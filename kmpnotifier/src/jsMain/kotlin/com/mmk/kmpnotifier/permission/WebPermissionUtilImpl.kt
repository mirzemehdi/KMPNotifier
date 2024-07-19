package com.mmk.kmpnotifier.permission

import org.w3c.notifications.GRANTED
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationPermission

internal class WebPermissionUtilImpl : PermissionUtil {
    override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        val permission = Notification.permission
        onPermissionResult(permission == NotificationPermission.GRANTED)
    }

    override fun askNotificationPermission(onPermissionGranted: () -> Unit) {
        Notification.requestPermission().then {
            onPermissionGranted()
            null
        }
    }
}