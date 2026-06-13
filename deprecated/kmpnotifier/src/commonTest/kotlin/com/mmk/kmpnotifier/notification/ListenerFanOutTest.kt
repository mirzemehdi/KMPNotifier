package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.testutil.RecordingListener
import com.mmk.kmpnotifier.testutil.TestDispatch
import com.mmk.kmpnotifier.testutil.TestNotifierState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ListenerFanOutTest {

    @BeforeTest
    fun setUp() = TestNotifierState.resetAll()

    @AfterTest
    fun tearDown() = TestNotifierState.resetAll()

    @Test
    fun addListenerAccumulatesListeners() {
        val first = RecordingListener()
        val second = RecordingListener()
        NotifierManager.addListener(first)
        NotifierManager.addListener(second)

        TestDispatch.newToken("token-1")

        assertEquals(listOf("token-1"), first.newTokens)
        assertEquals(listOf("token-1"), second.newTokens)
    }

    @Test
    fun setListenerReplacesAllListeners() {
        val first = RecordingListener()
        val second = RecordingListener()
        NotifierManager.addListener(first)
        NotifierManager.setListener(second)

        TestDispatch.newToken("token-1")

        assertEquals(0, first.totalEventCount)
        assertEquals(listOf("token-1"), second.newTokens)
    }

    @Test
    fun setListenerNullClearsListeners() {
        val first = RecordingListener()
        NotifierManager.addListener(first)
        NotifierManager.setListener(null)

        TestDispatch.newToken("token-1")
        TestDispatch.notificationClicked(mapOf("k" to "v"))

        assertEquals(0, first.totalEventCount)
    }

    @Test
    fun onNewTokenFansOutExactToken() {
        val listener = RecordingListener()
        NotifierManager.addListener(listener)

        TestDispatch.newToken("abc")

        assertEquals(listOf("abc"), listener.newTokens)
        assertEquals(1, listener.totalEventCount)
    }

    @Test
    fun onPayloadDataFansOutExactData() {
        val listener = RecordingListener()
        NotifierManager.addListener(listener)
        val data = mapOf("key1" to "value1", "key2" to 2)

        TestDispatch.pushPayloadData(data)

        assertEquals(listOf<PayloadData>(data), listener.payloadData)
        assertEquals(1, listener.totalEventCount)
    }

    @Test
    fun onPushNotificationFansOutTitleAndBody() {
        val listener = RecordingListener()
        NotifierManager.addListener(listener)

        TestDispatch.pushNotification(title = "title", body = "body")

        assertEquals(listOf<Pair<String?, String?>>("title" to "body"), listener.pushNotifications)
    }

    @Test
    fun onPushNotificationSupportsNullTitleAndBody() {
        val listener = RecordingListener()
        NotifierManager.addListener(listener)

        TestDispatch.pushNotification(title = null, body = null)

        assertEquals(1, listener.pushNotifications.size)
        assertNull(listener.pushNotifications.single().first)
        assertNull(listener.pushNotifications.single().second)
    }

    @Test
    fun onPushNotificationWithPayloadDataFansOutAllArgs() {
        val listener = RecordingListener()
        NotifierManager.addListener(listener)
        val data = mapOf("k" to "v")

        TestDispatch.pushNotificationWithPayloadData(title = "t", body = "b", data = data)

        assertEquals(listOf(Triple<String?, String?, PayloadData>("t", "b", data)), listener.pushNotificationsWithPayload)
    }

    @Test
    fun onPushNotificationWithPayloadDataDefaultsTitleBodyToNull() {
        val listener = RecordingListener()
        NotifierManager.addListener(listener)
        val data = mapOf("k" to "v")

        TestDispatch.pushNotificationWithPayloadData(data = data)

        val event = listener.pushNotificationsWithPayload.single()
        assertNull(event.first)
        assertNull(event.second)
        assertEquals(data, event.third)
    }

    @Test
    fun onNotificationClickedFansOutData() {
        val listener = RecordingListener()
        NotifierManager.addListener(listener)
        val data = mapOf("url" to "https://example.com")

        TestDispatch.notificationClicked(data)

        assertEquals(listOf<PayloadData>(data), listener.clicks)
    }

    @Test
    fun onActionFansOutAllArgs() {
        val listener = RecordingListener()
        NotifierManager.addListener(listener)
        val payload = mapOf<String, Any?>("k" to "v", "n" to null)

        TestDispatch.action(actionId = "reply", notificationId = 42, payload = payload)

        assertEquals(listOf(Triple("reply", 42, payload)), listener.actions)
    }

}
