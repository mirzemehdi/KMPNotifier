package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.permission.IosPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.UserNotifications.UNUserNotificationCenter


internal actual val platformModule = module {
    factory { Platform.Ios } bind Platform::class
    factory { IosPermissionUtil(notificationCenter = UNUserNotificationCenter.currentNotificationCenter()) } bind PermissionUtil::class
}
