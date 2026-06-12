package com.mmk.kmpnotifier.push

import com.mmk.kmpnotifier.notification.PayloadData

/**
 * Push notification events.
 *
 * Register with `KMPNotifier.addPushListener(...)` (or `FirebasePush.addListener(...)`).
 * Shared events (notification clicks, action buttons) are part of the core API —
 * see `KMPNotifier.Listener`.
 */
public interface PushListener {

    /**
     * Called when the push notification token is updated, or initialized the first time.
     * @param token push notification token
     */
    public fun onNewToken(token: String) {}

    /**
     * Called when a data-type push message is available.
     * @param data push notification payload data
     */
    public fun onPayloadData(data: PayloadData) {}

    /**
     * Called when a notification-type push message is received.
     * @param title notification title
     * @param body notification body message
     */
    public fun onPushNotification(title: String?, body: String?) {}

    /**
     * Called when a push notification is received, with both notification content and payload.
     * @param title notification title
     * @param body notification body message
     * @param data notification payload data
     */
    public fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {}
}
