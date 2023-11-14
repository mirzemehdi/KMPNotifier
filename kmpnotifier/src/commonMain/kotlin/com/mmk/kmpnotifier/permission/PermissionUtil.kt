package com.mmk.kmpnotifier.permission

/**
 * Permission utility class
 */
internal interface PermissionUtil {


    fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit = {})
    fun askNotificationPermission(onPermissionGranted: () -> Unit = {})
}