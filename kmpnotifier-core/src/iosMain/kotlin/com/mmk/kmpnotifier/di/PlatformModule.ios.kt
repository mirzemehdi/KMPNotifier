package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.permission.IosPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil
import platform.UserNotifications.UNUserNotificationCenter


internal actual val platform: Platform = Platform.Ios

internal actual fun createPermissionUtil(): PermissionUtil =
    IosPermissionUtil(notificationCenter = UNUserNotificationCenter.currentNotificationCenter())
