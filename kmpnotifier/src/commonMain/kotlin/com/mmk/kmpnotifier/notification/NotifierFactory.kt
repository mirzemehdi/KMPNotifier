package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.di.KMPKoinComponent
import com.mmk.kmpnotifier.di.LibDependencyInitializer
import org.koin.core.component.get

public object NotifierFactory : KMPKoinComponent() {
    /**
     * Call initialize function on App Start.
     * @param configuration pass either ios or android configuration depending on platform
     * @see NotificationPlatformConfiguration.Ios
     * @see NotificationPlatformConfiguration.Android
     */
    public fun initialize(configuration: NotificationPlatformConfiguration) {
        LibDependencyInitializer.initialize(configuration)
    }

    /**
     * Creates new Notifier instance
     */
    public fun create(): Notifier {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "NotifierFactory is not initialized. " +
                    "Please, initialize NotifierFactory by calling #initialize method"
        )
        return get()
    }

}