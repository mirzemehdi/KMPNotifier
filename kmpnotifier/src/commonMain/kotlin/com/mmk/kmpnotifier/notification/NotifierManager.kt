package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration


public object NotifierManager {

    /**
     * Call initialize function on App Start.
     * @param configuration pass either ios or android configuration depending on platform
     * @see NotificationPlatformConfiguration.Ios
     * @see NotificationPlatformConfiguration.Android
     */
    public fun initialize(configuration: NotificationPlatformConfiguration) {
        NotifierManagerImpl.initialize(configuration)
    }

    /**
     * Creates local Notifier instance
     */
    public fun getLocalNotifier(): Notifier {
        return NotifierManagerImpl.getLocalNotifier()
    }

    /**
     * Creates push Notifier instance
     */
    public fun getPushNotifier(): PushNotifier {
        return NotifierManagerImpl.getPushNotifier()
    }

    public fun addListener(listener: Listener) {
        NotifierManagerImpl.addListener(listener)
    }


    public interface Listener {
        public fun onNewToken(token: String)
    }


}