package com.mmk.kmpnotifier.permission

internal class EmptyPermissionUtilImpl : PermissionUtil {
    override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        println("Not implemented: has permission result: true")
        onPermissionResult(true)
    }

    override fun askNotificationPermission(onPermissionGranted: () -> Unit) {
        println("Not implemented: granted permission by default")
        onPermissionGranted()
    }
}