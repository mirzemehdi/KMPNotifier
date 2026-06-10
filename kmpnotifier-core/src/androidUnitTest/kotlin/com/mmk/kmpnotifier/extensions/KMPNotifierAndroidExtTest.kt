@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.extensions

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.mmk.kmpnotifier.Constants
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.testutil.RecordingCoreListener
import com.mmk.kmpnotifier.testutil.RecordingPushSink
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class KMPNotifierAndroidExtTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val configuration = NotificationPlatformConfiguration.Android(
        notificationIconResId = android.R.drawable.ic_dialog_info,
    )

    private lateinit var listener: RecordingCoreListener
    private lateinit var pushSink: RecordingPushSink

    @Before
    fun setUp() {
        NotifierInternals.resetForTests()
        listener = RecordingCoreListener()
        pushSink = RecordingPushSink()
        KMPNotifier.addListener(listener)
        NotifierEventHub.registerPushEventSink(pushSink)
    }

    @After
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun initializeWithContextInitializesLibrary() {
        KMPNotifier.initialize(context, configuration)
        assertTrue(KMPNotifier.isInitialized)
        KMPNotifier.permissionUtil // resolves without throwing
    }

    @Test
    fun permissionUtilReportsGrantedPermission() {
        shadowOf(context as Application).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        KMPNotifier.initialize(context, configuration)

        var result: Boolean? = null
        KMPNotifier.permissionUtil.hasNotificationPermission { result = it }
        assertTrue(result == true, "Expected granted notification permission, got $result")
    }

    @Test
    fun nullIntentEmitsNothing() {
        KMPNotifier.onCreateOrOnNewIntent(null)
        assertEquals(0, listener.totalEventCount)
        assertEquals(0, pushSink.totalEventCount)
    }

    @Test
    fun clickExtraEmitsNotificationClickedWithClickKeyStripped() {
        val intent = Intent()
            .putExtra(Constants.ACTION_NOTIFICATION_CLICK, Constants.ACTION_NOTIFICATION_CLICK)
            .putExtra("customKey", "customValue")

        KMPNotifier.onCreateOrOnNewIntent(intent)

        val clickedPayload = listener.clicks.single()
        assertEquals("customValue", clickedPayload["customKey"])
        assertFalse(clickedPayload.containsKey(Constants.ACTION_NOTIFICATION_CLICK))
        assertEquals(0, pushSink.totalEventCount)
    }

    @Test
    fun firebaseMarkerEmitsPushEventsAndClick() {
        val intent = Intent()
            .putExtra(Constants.KEY_ANDROID_FIREBASE_NOTIFICATION, 12345L)
            .putExtra("customKey", "customValue")

        KMPNotifier.onCreateOrOnNewIntent(intent)

        assertEquals(1, pushSink.payloadData.size)
        assertEquals("customValue", pushSink.payloadData.single()["customKey"])
        assertEquals(1, pushSink.pushNotificationsWithPayload.size)
        assertEquals(1, listener.clicks.size)
    }
}
