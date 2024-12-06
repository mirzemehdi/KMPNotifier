package com.mmk.kmpnotifier.notification

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

}