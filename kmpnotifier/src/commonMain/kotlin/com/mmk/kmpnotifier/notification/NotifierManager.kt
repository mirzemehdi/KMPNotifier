package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration


public object NotifierManager {

    /**
     * Call initialize function on Application Start.
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
     * Creates push Notifier instance (Firebase Push Notification)
     */
    public fun getPushNotifier(): PushNotifier {
        return NotifierManagerImpl.getPushNotifier()
    }

    /**
     * For listening updates such as push notification token changes
     */
    public fun addListener(listener: Listener) {
        NotifierManagerImpl.addListener(listener)
    }


    public interface Listener {
        /**
         * Called when push notification token is updated, or initialized first time
         * @param token Push Notification token
         */
        public fun onNewToken(token: String) {}

        /**
         * Called when "Push Notification" data type message is available
         * @see onPushNotification for receiving "Push Notification" notification type message.
         * @param data Push Notification Payload Data
         */
        public fun onPayloadData(data: PayloadData) {}

        /**
         * Called when "Push Notification" notification type message is received.
         * @see onPayloadData for receiving "Push Notification" data type message.
         * @param title Notification title
         * @param body Notification body message
         */
        public fun onPushNotification(title:String?,body:String?) {}
    }


}