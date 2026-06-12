@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.push.firebase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.extensions.initialize
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.local.LocalNotifications
import com.mmk.kmpnotifier.local.localNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FirebasePushInstallTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val configuration = NotificationPlatformConfiguration.Android(
        notificationIconResId = android.R.drawable.ic_dialog_info,
    )

    @Before
    fun setUp() = NotifierInternals.resetForTests()

    @After
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun installingFirebasePushProvidesPushNotifier() {
        KMPNotifier.initialize(context, configuration, FirebasePush)
        assertEquals("FirebasePushNotifierImpl", KMPNotifier.firebasePushNotifier::class.simpleName)
        assertSame(KMPNotifier.firebasePushNotifier, FirebasePush.notifier)
    }

    @Test
    fun firebasePushInstallsLocalNotificationsAsDependency() {
        KMPNotifier.initialize(context, configuration, FirebasePush)
        assertEquals("AndroidNotifier", LocalNotifications.notifier::class.simpleName)
        assertSame(LocalNotifications.notifier, KMPNotifier.localNotifier)
    }
}
