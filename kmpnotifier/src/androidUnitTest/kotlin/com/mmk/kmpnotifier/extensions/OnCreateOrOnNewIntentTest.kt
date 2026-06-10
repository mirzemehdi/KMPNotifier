package com.mmk.kmpnotifier.extensions

import android.content.Intent
import com.mmk.kmpnotifier.Constants
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.testutil.RecordingListener
import com.mmk.kmpnotifier.testutil.TestNotifierState
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OnCreateOrOnNewIntentTest {

    private lateinit var listener: RecordingListener

    @Before
    fun setUp() {
        TestNotifierState.resetAll()
        listener = RecordingListener()
        NotifierManager.addListener(listener)
    }

    @After
    fun tearDown() = TestNotifierState.resetAll()

    @Test
    fun nullIntentEmitsNothing() {
        NotifierManager.onCreateOrOnNewIntent(null)
        assertEquals(0, listener.totalEventCount)
    }

    @Test
    fun intentWithoutExtrasEmitsNothing() {
        NotifierManager.onCreateOrOnNewIntent(Intent())
        assertEquals(0, listener.totalEventCount)
    }

    @Test
    fun clickExtraEmitsNotificationClickedWithClickKeyStripped() {
        val intent = Intent()
            .putExtra(Constants.ACTION_NOTIFICATION_CLICK, Constants.ACTION_NOTIFICATION_CLICK)
            .putExtra("customKey", "customValue")

        NotifierManager.onCreateOrOnNewIntent(intent)

        assertEquals(1, listener.clicks.size)
        val clickedPayload = listener.clicks.single()
        assertEquals("customValue", clickedPayload["customKey"])
        assertFalse(clickedPayload.containsKey(Constants.ACTION_NOTIFICATION_CLICK))
        // No push events for a local notification click.
        assertEquals(0, listener.payloadData.size)
        assertEquals(0, listener.pushNotificationsWithPayload.size)
    }

    @Test
    fun firebaseMarkerEmitsPushEventsAndClick() {
        val intent = Intent()
            .putExtra(Constants.KEY_ANDROID_FIREBASE_NOTIFICATION, 12345L)
            .putExtra("customKey", "customValue")

        NotifierManager.onCreateOrOnNewIntent(intent)

        assertEquals(1, listener.payloadData.size)
        assertEquals("customValue", listener.payloadData.single()["customKey"])
        assertTrue(listener.payloadData.single().containsKey(Constants.KEY_ANDROID_FIREBASE_NOTIFICATION))

        assertEquals(1, listener.pushNotificationsWithPayload.size)
        val (title, body, data) = listener.pushNotificationsWithPayload.single()
        assertNull(title)
        assertNull(body)
        assertEquals("customValue", data["customKey"])

        assertEquals(1, listener.clicks.size)
    }

    @Test
    fun firebaseMarkerWithClickActionStripsClickKeyFromPushPayload() {
        val intent = Intent()
            .putExtra(Constants.KEY_ANDROID_FIREBASE_NOTIFICATION, 12345L)
            .putExtra(Constants.ACTION_NOTIFICATION_CLICK, Constants.ACTION_NOTIFICATION_CLICK)
            .putExtra("customKey", "customValue")

        NotifierManager.onCreateOrOnNewIntent(intent)

        assertFalse(listener.payloadData.single().containsKey(Constants.ACTION_NOTIFICATION_CLICK))
        assertFalse(
            listener.pushNotificationsWithPayload.single().third
                .containsKey(Constants.ACTION_NOTIFICATION_CLICK)
        )
        assertFalse(listener.clicks.single().containsKey(Constants.ACTION_NOTIFICATION_CLICK))
    }
}
