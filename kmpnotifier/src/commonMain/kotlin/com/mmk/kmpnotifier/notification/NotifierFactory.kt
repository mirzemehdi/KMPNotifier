package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

public object NotifierFactory {


    /**
     * Call initialize function on App Start.
     * @param configuration pass either ios or android configuration depending on platform
     * @see NotificationPlatformConfiguration.Ios
     * @see NotificationPlatformConfiguration.Android
     */
    public fun initialize(configuration: NotificationPlatformConfiguration) {
        NotifierFactoryImpl.initialize(configuration)
    }

    /**
     * Creates new Notifier instance
     */
    public fun create(): Notifier {
        return NotifierFactoryImpl.create()
    }


}