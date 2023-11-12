package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.di.KMPKoinComponent
import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.koin.core.component.get

internal object NotifierFactoryImpl : KMPKoinComponent() {

    fun initialize(configuration: NotificationPlatformConfiguration) {
        LibDependencyInitializer.initialize(configuration)
    }

    fun create(): Notifier {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "NotifierFactory is not initialized. " +
                    "Please, initialize NotifierFactory by calling #initialize method"
        )
        return get()
    }

}