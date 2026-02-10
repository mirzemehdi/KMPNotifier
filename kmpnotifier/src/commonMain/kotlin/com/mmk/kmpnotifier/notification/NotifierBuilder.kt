package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.NotificationAction
import kotlin.random.Random

public class NotifierBuilder {

    public var id: Int = Random.nextInt(0, Int.MAX_VALUE)
    public var title: String = ""
    public var body: String = ""

    public var payloadData: Map<String, String> = emptyMap()

    public var image: NotificationImage? = null

    public fun payload(block: MutableMap<String, String>.() -> Unit) {
        payloadData = mutableMapOf<String, String>().apply(block)
    }

    /**
     * List of action buttons to display with the notification
     */
    public var actions: List<NotificationAction> = emptyList()

    public var scheduledAt: Long = 0L //0L means show immediately

}