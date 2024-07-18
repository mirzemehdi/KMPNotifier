package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.notification.EmptyPushNotifierImpl
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.WebConsoleNotifier
import com.mmk.kmpnotifier.permission.EmptyPermissionUtilImpl
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal actual val platformModule = module {
    factory { Platform.Web } bind Platform::class
    factoryOf(::EmptyPermissionUtilImpl) bind PermissionUtil::class
    factoryOf(::WebConsoleNotifier) bind Notifier::class
    factoryOf(::EmptyPushNotifierImpl) bind PushNotifier::class
}