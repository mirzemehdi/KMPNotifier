package com.mmk.kmpnotifier.notification.configuration

/**
 * You can provide your own implementation of the notification UI
 */
public interface DisplayNotificationManager {

    /**
     * Implement mechanism of notification displaying
     *
     * @param title of the notification
     * @param body of the notification
     */
    public fun displayNotification(title: String, body: String)
}
