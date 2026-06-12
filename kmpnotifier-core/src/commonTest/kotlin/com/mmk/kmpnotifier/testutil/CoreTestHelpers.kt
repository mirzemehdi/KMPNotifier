@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.testutil

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.PushEventSink
import com.mmk.kmpnotifier.notification.PayloadData

/** Records shared notification events for assertions. */
internal class RecordingCoreListener : KMPNotifier.Listener {
    val clicks = mutableListOf<PayloadData>()
    val actions = mutableListOf<Triple<String, Int, PayloadData>>()

    val totalEventCount: Int get() = clicks.size + actions.size

    override fun onNotificationClicked(data: PayloadData) {
        clicks.add(data)
    }

    override fun onAction(actionId: String, notificationId: Int, payload: PayloadData) {
        actions.add(Triple(actionId, notificationId, payload))
    }
}

/** Records push events for assertions. */
internal class RecordingPushSink : PushEventSink {
    val tokens = mutableListOf<String>()
    val payloadData = mutableListOf<PayloadData>()
    val pushNotifications = mutableListOf<Pair<String?, String?>>()
    val pushNotificationsWithPayload = mutableListOf<Triple<String?, String?, PayloadData>>()

    val totalEventCount: Int
        get() = tokens.size + payloadData.size + pushNotifications.size + pushNotificationsWithPayload.size

    override fun onNewToken(token: String) {
        tokens.add(token)
    }

    override fun onPushPayloadData(data: PayloadData) {
        payloadData.add(data)
    }

    override fun onPushNotification(title: String?, body: String?) {
        pushNotifications.add(title to body)
    }

    override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
        pushNotificationsWithPayload.add(Triple(title, body, data))
    }
}
