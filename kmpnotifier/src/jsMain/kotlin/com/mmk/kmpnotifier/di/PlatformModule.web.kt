package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.notification.EmptyPushNotifierImpl
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.WebConsoleNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.EmptyPermissionUtilImpl
import com.mmk.kmpnotifier.permission.PermissionUtil
import com.mmk.kmpnotifier.permission.WebPermissionUtilImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal actual val platformModule = module {
    factory { Platform.Web } bind Platform::class
    factoryOf(::WebPermissionUtilImpl) bind PermissionUtil::class
    factory {
        val configuration =
            get<NotificationPlatformConfiguration>() as NotificationPlatformConfiguration.Web
        WebConsoleNotifier(configuration = configuration, permissionUtil = get())
    } bind Notifier::class
    factoryOf(::EmptyPushNotifierImpl) bind PushNotifier::class
}