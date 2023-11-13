package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.firebase.FirebasePushNotifierImpl
import com.mmk.kmpnotifier.notification.IosNotifier
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.permission.IosPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.UserNotifications.UNUserNotificationCenter

internal actual fun isAndroidPlatform(): Boolean = false

internal actual val platformModule = module {
    factory { IosPermissionUtil(notificationCenter = UNUserNotificationCenter.currentNotificationCenter()) } bind PermissionUtil::class
    factory {
        IosNotifier(
            permissionUtil = get(),
            notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        )
    } bind Notifier::class

    factory {
        FirebasePushNotifierImpl()
    } bind PushNotifier::class
}