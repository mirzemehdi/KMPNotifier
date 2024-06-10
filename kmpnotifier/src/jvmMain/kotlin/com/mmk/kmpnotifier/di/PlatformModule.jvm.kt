package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.firebase.FirebaseDesktopPushNotifier
import com.mmk.kmpnotifier.notification.DesktopNotifier
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.DesktopPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    factory { Platform.Desktop } bind Platform::class

    factory {
        val configuration =
            get<NotificationPlatformConfiguration>() as NotificationPlatformConfiguration.Desktop
        DesktopNotifier(desktopNotificationConfiguration = configuration)
    } bind Notifier::class
    factoryOf(::DesktopPermissionUtil) bind PermissionUtil::class
    factoryOf(::FirebaseDesktopPushNotifier) bind PushNotifier::class
}