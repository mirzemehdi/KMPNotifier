@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.internal

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.notification.PayloadData
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the relative ordering of events flowing through the hub. The iOS notification-click
 * path emits action → push events → clicked in that order; the order must be preserved
 * for any listener observing multiple event kinds.
 */
class EventOrderingTest {

    private class OrderRecorder : KMPNotifier.Listener, PushEventSink {
        val order = mutableListOf<String>()

        override fun onNotificationClicked(data: PayloadData) {
            order.add("clicked")
        }

        override fun onAction(actionId: String, notificationId: Int, payload: PayloadData) {
            order.add("action")
        }

        override fun onNewToken(token: String) {
            order.add("token")
        }

        override fun onPushPayloadData(data: PayloadData) {
            order.add("pushPayload")
        }

        override fun onPushNotification(title: String?, body: String?) {
            order.add("pushNotification")
        }

        override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
            order.add("pushWithPayload")
        }
    }

    @BeforeTest
    fun setUp() = NotifierInternals.resetForTests()

    @AfterTest
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun iosClickPathOrderIsPreserved() {
        val recorder = OrderRecorder()
        KMPNotifier.addListener(recorder)
        NotifierEventHub.registerPushEventSink(recorder)

        // Emission sequence of IosNotifier.NotificationDelegate.didReceive for a remote
        // notification with a custom action.
        NotifierEventHub.emitAction("reply", 1, mapOf("k" to "v"))
        NotifierEventHub.emitPushNotification("t", "b")
        NotifierEventHub.emitPushPayloadData(mapOf("k" to "v"))
        NotifierEventHub.emitPushNotificationWithPayloadData(title = "t", body = "b", data = mapOf("k" to "v"))
        NotifierEventHub.emitNotificationClicked(mapOf("k" to "v"))

        assertEquals(
            listOf("action", "pushNotification", "pushPayload", "pushWithPayload", "clicked"),
            recorder.order,
        )
    }

    @Test
    fun eachEventIsDeliveredExactlyOncePerListener() {
        val recorder = OrderRecorder()
        KMPNotifier.addListener(recorder)
        NotifierEventHub.registerPushEventSink(recorder)
        // Re-registering must not duplicate delivery (set semantics).
        KMPNotifier.addListener(recorder)
        NotifierEventHub.registerPushEventSink(recorder)

        NotifierEventHub.emitNotificationClicked(mapOf("k" to "v"))
        NotifierEventHub.emitNewToken("token")

        assertEquals(listOf("clicked", "token"), recorder.order)
    }
}
