package com.mmk.kmpnotifier.permission

internal interface PermissionUtil {
    fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit = {})
    fun askNotificationPermission(onPermissionGranted: () -> Unit = {})
}