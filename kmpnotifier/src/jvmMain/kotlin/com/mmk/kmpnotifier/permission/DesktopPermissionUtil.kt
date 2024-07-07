package com.mmk.kmpnotifier.permission

internal class DesktopPermissionUtil:PermissionUtil {
    override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        println("Desktop has permission result")
        onPermissionResult(true)
    }

    override fun askNotificationPermission(onPermissionGranted: () -> Unit) {
        println("Desktop ask permission")
        onPermissionGranted()
    }
}