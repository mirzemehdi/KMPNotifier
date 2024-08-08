package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.firebase.FirebasePushNotifierImpl
import com.mmk.kmpnotifier.notification.IosNotifier
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.IosPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.UserNotifications.UNUserNotificationCenter


internal actual val platformModule = module {
    factory { Platform.Ios } bind Platform::class
    factory { IosPermissionUtil(notificationCenter = UNUserNotificationCenter.currentNotificationCenter()) } bind PermissionUtil::class
    factory {
        val configuration =
            get<NotificationPlatformConfiguration>() as NotificationPlatformConfiguration.Ios
        IosNotifier(
            permissionUtil = get(),
            notificationCenter = UNUserNotificationCenter.currentNotificationCenter(),
            iosNotificationConfiguration = configuration
        )
    } bind Notifier::class

    factory {
        FirebasePushNotifierImpl()
    } bind PushNotifier::class


}