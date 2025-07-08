package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.PushNotifierImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal actual val platformModule = module {
    commonPlatformModule()
    factoryOf(::PushNotifierImpl) bind PushNotifier::class
}
