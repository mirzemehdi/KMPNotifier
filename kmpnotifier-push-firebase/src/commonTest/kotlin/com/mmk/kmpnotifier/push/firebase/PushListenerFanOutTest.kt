@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.push.firebase

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.push.PushListener
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PushListenerFanOutTest {

    private class RecordingPushListener : PushListener {
        val tokens = mutableListOf<String>()
        val payloadData = mutableListOf<PayloadData>()
        val pushNotifications = mutableListOf<Pair<String?, String?>>()
        val pushNotificationsWithPayload = mutableListOf<Triple<String?, String?, PayloadData>>()

        val totalEventCount: Int
            get() = tokens.size + payloadData.size + pushNotifications.size + pushNotificationsWithPayload.size

        override fun onNewToken(token: String) {
            tokens.add(token)
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
    }

    @BeforeTest
    fun setUp() {
        NotifierInternals.resetForTests()
        FirebasePush.setListener(null)
    }

    @AfterTest
    fun tearDown() {
        NotifierInternals.resetForTests()
        FirebasePush.setListener(null)
    }

    @Test
    fun pushEventsReachRegisteredListeners() {
        val listener = RecordingPushListener()
        FirebasePush.addListener(listener)
        val data = mapOf("k" to "v")

        NotifierEventHub.emitNewToken("token-1")
        NotifierEventHub.emitPushPayloadData(data)
        NotifierEventHub.emitPushNotification("title", "body")
        NotifierEventHub.emitPushNotificationWithPayloadData(title = "title", body = "body", data = data)

        assertEquals(listOf("token-1"), listener.tokens)
        assertEquals(listOf<PayloadData>(data), listener.payloadData)
        assertEquals(listOf<Pair<String?, String?>>("title" to "body"), listener.pushNotifications)
        assertEquals(
            listOf(Triple<String?, String?, PayloadData>("title", "body", data)),
            listener.pushNotificationsWithPayload,
        )
    }

    @Test
    fun addListenerWorksBeforeInitialization() {
        val listener = RecordingPushListener()
        FirebasePush.addListener(listener)
        NotifierEventHub.emitNewToken("early-token")
        assertEquals(listOf("early-token"), listener.tokens)
    }

    @Test
    fun setListenerReplacesAllListeners() {
        val first = RecordingPushListener()
        val second = RecordingPushListener()
        FirebasePush.addListener(first)
        FirebasePush.setListener(second)

        NotifierEventHub.emitNewToken("token")

        assertEquals(0, first.totalEventCount)
        assertEquals(listOf("token"), second.tokens)
    }

    @Test
    fun removeListenerStopsDelivery() {
        val listener = RecordingPushListener()
        FirebasePush.addListener(listener)
        FirebasePush.removeListener(listener)

        NotifierEventHub.emitNewToken("token")

        assertEquals(0, listener.totalEventCount)
    }

    @Test
    fun addingSameListenerTwiceDeliversOnce() {
        val listener = RecordingPushListener()
        FirebasePush.addListener(listener)
        FirebasePush.addListener(listener)

        NotifierEventHub.emitNewToken("token")

        assertEquals(listOf("token"), listener.tokens)
    }

    @Test
    fun notifierThrowsWhenPushNotInstalled() {
        assertFailsWith<IllegalStateException> { FirebasePush.notifier }
    }
}
