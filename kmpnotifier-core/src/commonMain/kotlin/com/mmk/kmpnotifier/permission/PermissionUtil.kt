package com.mmk.kmpnotifier.permission

/**
 * Permission utility class
 */
public interface PermissionUtil {


    /** Checks whether notification permission is granted and reports the result via [onPermissionResult]. */
    public fun hasNotificationPermission(onPermissionResult: (isGranted: Boolean) -> Unit = {})

    /**
     * Asks for notification permission and reports the result via [onPermissionResult].
     * On Android, runtime permission must be requested in an Activity — see `ComponentActivity.permissionUtil()`.
     */
    public fun askNotificationPermission(onPermissionResult: (isGranted: Boolean) -> Unit = {})
}