@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.testutil.RecordingCoreListener
import com.mmk.kmpnotifier.testutil.RecordingPushSink
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedListenerFanOutTest {

    @BeforeTest
    fun setUp() = NotifierInternals.resetForTests()

    @AfterTest
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun addListenerAccumulatesListeners() {
        val first = RecordingCoreListener()
        val second = RecordingCoreListener()
        KMPNotifier.addListener(first)
        KMPNotifier.addListener(second)

        NotifierEventHub.emitNotificationClicked(mapOf("k" to "v"))

        assertEquals(1, first.clicks.size)
        assertEquals(1, second.clicks.size)
    }

    @Test
    fun removeListenerStopsDelivery() {
        val listener = RecordingCoreListener()
        KMPNotifier.addListener(listener)
        KMPNotifier.removeListener(listener)

        NotifierEventHub.emitNotificationClicked(mapOf("k" to "v"))

        assertEquals(0, listener.totalEventCount)
    }

    @Test
    fun setListenerReplacesAllListeners() {
        val first = RecordingCoreListener()
        val second = RecordingCoreListener()
        KMPNotifier.addListener(first)
        KMPNotifier.setListener(second)

        NotifierEventHub.emitAction("action", 1, mapOf("k" to "v"))

        assertEquals(0, first.totalEventCount)
        assertEquals(1, second.actions.size)
    }

    @Test
    fun setListenerNullClearsListeners() {
        val listener = RecordingCoreListener()
        KMPNotifier.addListener(listener)
        KMPNotifier.setListener(null)

        NotifierEventHub.emitNotificationClicked(mapOf("k" to "v"))

        assertEquals(0, listener.totalEventCount)
    }

    @Test
    fun clickedAndActionEventsCarryExactArguments() {
        val listener = RecordingCoreListener()
        KMPNotifier.addListener(listener)
        val payload = mapOf<String, Any?>("k" to "v", "n" to null)

        NotifierEventHub.emitNotificationClicked(mapOf("url" to "https://example.com"))
        NotifierEventHub.emitAction(actionId = "reply", notificationId = 42, payload = payload)

        assertEquals(mapOf("url" to "https://example.com"), listener.clicks.single())
        assertEquals(Triple("reply", 42, payload), listener.actions.single())
    }

    @Test
    fun pushEventsDoNotReachSharedListeners() {
        val listener = RecordingCoreListener()
        KMPNotifier.addListener(listener)

        NotifierEventHub.emitNewToken("token")
        NotifierEventHub.emitPushPayloadData(mapOf("k" to "v"))
        NotifierEventHub.emitPushNotification("t", "b")
        NotifierEventHub.emitPushNotificationWithPayloadData(data = mapOf("k" to "v"))

        assertEquals(0, listener.totalEventCount)
    }

    @Test
    fun sharedEventsDoNotReachPushSinks() {
        val sink = RecordingPushSink()
        NotifierEventHub.registerPushEventSink(sink)

        NotifierEventHub.emitNotificationClicked(mapOf("k" to "v"))
        NotifierEventHub.emitAction("a", 1, mapOf("k" to "v"))

        assertEquals(0, sink.totalEventCount)
    }

    @Test
    fun setListenerNullDoesNotAffectInternalListeners() {
        val internalListener = RecordingCoreListener()
        NotifierEventHub.registerInternalListener(internalListener)
        KMPNotifier.setListener(null)

        NotifierEventHub.emitNotificationClicked(mapOf("k" to "v"))

        assertEquals(1, internalListener.clicks.size)
    }

    @Test
    fun dispatchWithoutListenersDoesNotThrow() {
        NotifierEventHub.emitNotificationClicked(mapOf("k" to "v"))
        NotifierEventHub.emitAction("a", 1, mapOf("k" to "v"))
        NotifierEventHub.emitNewToken("token")
        NotifierEventHub.emitPushPayloadData(mapOf("k" to "v"))
        NotifierEventHub.emitPushNotification("t", "b")
        NotifierEventHub.emitPushNotificationWithPayloadData(data = mapOf("k" to "v"))
    }
}
