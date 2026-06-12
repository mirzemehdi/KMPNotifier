package com.mmk.kmpnotifier.notification

/**
 * Class that represent local notification
 */
public interface Notifier {

    public companion object {
        /** Payload key whose value (a URL) is attached as intent data on Android for deep linking. */
        public var KEY_URL: String = "URL"
    }

    /**
     * Sends local notification to device
     * @param title Title part
     * @param body Body part
     * @param payloadData Extra payload data information.
     * @return notification id
     */
    public fun notify(
        title: String,
        body: String,
        payloadData: Map<String, String> = emptyMap()
    ): Int

    /**
     * Sends local notification to device with id
     * @param id notification id
     * @param title Title part
     * @param body Body part
     * @param payloadData Extra payload data information
     */
    public fun notify(
        id: Int,
        title: String,
        body: String,
        payloadData: Map<String, String> = emptyMap()
    )

    /**
     * Sends local notification to device,
     * with notification builder that allows you to set notification params
     */
    public fun notify(block: NotifierBuilder.() -> Unit)


    /**
     * Remove notification by id
     * @param id notification id
     */
    public fun remove(id: Int)

    /**
     * Removes all previously shown notifications
     * @see remove(id) for removing specific notification.
     */
    public fun removeAll()

    /**
     * Simple method to schedule a notification (backward compatibility)
     * @param title Notification title
     * @param body Notification body
     * @param fireAt When to fire the notification (milliseconds since epoch)
     * @return notification id
     */
    public fun scheduleSimple(title: String, body: String, fireAt: Long): Int {
        // This is a placeholder - actual scheduling will be handled by platform implementations
        // For now, just show immediately for backward compatibility
        return notify(title, body)
    }
}