package com.mmk.kmpnotifier.testutil

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData

/**
 * Listener that records every callback invocation for assertions.
 */
internal class RecordingListener : NotifierManager.Listener {

    val newTokens = mutableListOf<String>()
    val payloadData = mutableListOf<PayloadData>()
    val pushNotifications = mutableListOf<Pair<String?, String?>>()
    val pushNotificationsWithPayload = mutableListOf<Triple<String?, String?, PayloadData>>()
    val clicks = mutableListOf<PayloadData>()
    val actions = mutableListOf<Triple<String, Int, Map<String, Any?>>>()

    val totalEventCount: Int
        get() = newTokens.size + payloadData.size + pushNotifications.size +
                pushNotificationsWithPayload.size + clicks.size + actions.size

    override fun onNewToken(token: String) {
        newTokens.add(token)
    }

    override fun onPayloadData(data: PayloadData) {
        payloadData.add(data)
    }

    override fun onPushNotification(title: String?, body: String?) {
        pushNotifications.add(title to body)
    }

    override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
        pushNotificationsWithPayload.add(Triple(title, body, data))
    }

    override fun onNotificationClicked(data: PayloadData) {
        clicks.add(data)
    }

    override fun onAction(actionId: String, notificationId: Int, payload: Map<String, Any?>) {
        actions.add(Triple(actionId, notificationId, payload))
    }
}
