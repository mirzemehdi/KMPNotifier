package com.mmk.kmpnotifier.permission

import android.content.Context
import com.mmk.kmpnotifier.extensions.hasNotificationPermission


/**
 * This class is only for checking notification permission,
 * for asking runtime permission use AndroidPermissionUtil in your activity.
 */
internal class AndroidMockPermissionUtil(private val context: Context) : PermissionUtil {
    override fun hasNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
        onPermissionResult(context.hasNotificationPermission())
    }

    override fun askNotificationPermission(onPermissionGranted: () -> Unit) = Unit
}