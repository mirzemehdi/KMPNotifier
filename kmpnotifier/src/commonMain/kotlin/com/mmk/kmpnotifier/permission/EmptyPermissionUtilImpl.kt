package com.mmk.kmpnotifier.permission

import com.mmk.kmpnotifier.logger.currentLogger

internal class EmptyPermissionUtilImpl : PermissionUtil {
    override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        currentLogger.log("Not implemented: has permission result: true")
        onPermissionResult(true)
    }

    override fun askNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        currentLogger.log("Not implemented: granted permission by default")
        onPermissionResult(true)
    }
}
