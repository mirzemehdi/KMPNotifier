@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.PayloadData
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.ACTION_NOTIFICATION_ACTION
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_ACTION_ID
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_NOTIFICATION_ID
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_REMOTE_INPUT
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NotificationReceiverTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val actions = mutableListOf<Triple<String, Int, PayloadData>>()
    private lateinit var receiver: NotificationReceiver

    @Before
    fun setUp() {
        NotifierInternals.resetForTests()
        actions.clear()
        KMPNotifier.addListener(object : KMPNotifier.Listener {
            override fun onAction(actionId: String, notificationId: Int, payload: PayloadData) {
                actions.add(Triple(actionId, notificationId, payload))
            }
        })
        receiver = NotificationReceiver()
    }

    @After
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun actionIntentEmitsOnActionWithPayload() {
        val intent = Intent(ACTION_NOTIFICATION_ACTION)
            .putExtra(EXTRA_ACTION_ID, "reply")
            .putExtra(EXTRA_NOTIFICATION_ID, 5)
            .putExtra("customKey", "customValue")

        receiver.onReceive(context, intent)

        val (actionId, notificationId, payload) = actions.single()
        assertEquals("reply", actionId)
        assertEquals(5, notificationId)
        assertEquals("customValue", payload["customKey"])
    }

    @Test
    fun missingActionIdEmitsNothing() {
        val intent = Intent(ACTION_NOTIFICATION_ACTION)
            .putExtra(EXTRA_NOTIFICATION_ID, 5)

        receiver.onReceive(context, intent)

        assertEquals(0, actions.size)
    }

    @Test
    fun zeroNotificationIdEmitsNothing() {
        val intent = Intent(ACTION_NOTIFICATION_ACTION)
            .putExtra(EXTRA_ACTION_ID, "reply")
            .putExtra(EXTRA_NOTIFICATION_ID, 0)

        receiver.onReceive(context, intent)

        assertEquals(0, actions.size)
    }

    @Test
    fun remoteInputResultIsAddedToPayload() {
        val intent = Intent(ACTION_NOTIFICATION_ACTION)
            .putExtra(EXTRA_ACTION_ID, "reply")
            .putExtra(EXTRA_NOTIFICATION_ID, 6)
        val remoteInput = RemoteInput.Builder(EXTRA_REMOTE_INPUT).build()
        val results = Bundle().apply { putCharSequence(EXTRA_REMOTE_INPUT, "typed text") }
        RemoteInput.addResultsToIntent(arrayOf(remoteInput), intent, results)

        receiver.onReceive(context, intent)

        val (_, _, payload) = actions.single()
        assertEquals("typed text", payload["remote_input"])
    }

    @Test
    fun scheduledNotificationFiredEmitsOnAction() {
        val intent = Intent(ACTION_SCHEDULED_NOTIFICATION_FIRED)
            .putExtra(EXTRA_ACTION_ID, ACTION_SCHEDULED_NOTIFICATION_FIRED)
            .putExtra(EXTRA_NOTIFICATION_ID, 7)
            .putExtra("customKey", "customValue")

        receiver.onReceive(context, intent)

        val (actionId, notificationId, payload) = actions.single()
        assertEquals(ACTION_SCHEDULED_NOTIFICATION_FIRED, actionId)
        assertEquals(7, notificationId)
        assertEquals("customValue", payload["customKey"])
    }
}
