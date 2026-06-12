package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.NotificationAction
import kotlin.random.Random

/**
 * Builder used by [Notifier.notify] to configure a local notification.
 */
public class NotifierBuilder {

    /** Notification id. Defaults to a random non-negative value; pass your own to update or remove later. */
    public var id: Int = Random.nextInt(0, Int.MAX_VALUE)

    /** Notification title. */
    public var title: String = ""

    /** Notification body message. */
    public var body: String = ""

    /** Extra payload delivered back on click and action events. */
    public var payloadData: Map<String, String> = emptyMap()

    /** Optional large image, from a URL or a file path. */
    public var image: NotificationImage? = null

    /** DSL for building [payloadData]. */
    public fun payload(block: MutableMap<String, String>.() -> Unit) {
        payloadData = mutableMapOf<String, String>().apply(block)
    }

    /**
     * List of action buttons to display with the notification
     */
    public var actions: List<NotificationAction> = emptyList()

    /** Epoch millis (or millis from now for small values) to schedule the notification; 0 shows immediately. */
    public var scheduledAt: Long = 0L //0L means show immediately

}