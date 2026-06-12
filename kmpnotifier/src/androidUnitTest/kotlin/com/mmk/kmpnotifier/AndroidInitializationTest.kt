@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.mmk.kmpnotifier.extensions.initialize
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.testutil.TestNotifierState
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AndroidInitializationTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val configuration = NotificationPlatformConfiguration.Android(
        notificationIconResId = android.R.drawable.ic_dialog_info,
    )

    @Before
    fun setUp() = TestNotifierState.resetAll()

    @After
    fun tearDown() = TestNotifierState.resetAll()

    @Test
    fun initializeWithContextProvidesAndroidNotifier() {
        NotifierManager.initialize(context, configuration)
        assertEquals("AndroidNotifier", NotifierManager.getLocalNotifier()::class.simpleName)
    }

    @Test
    fun initializeWithContextProvidesFirebasePushNotifier() {
        NotifierManager.initialize(context, configuration)
        assertEquals("FirebasePushNotifierImpl", NotifierManager.getPushNotifier()::class.simpleName)
    }

    @Test
    fun initializeTwiceKeepsFirstConfiguration() {
        NotifierManager.initialize(context, configuration)
        NotifierManager.initialize(
            context,
            NotificationPlatformConfiguration.Android(notificationIconResId = android.R.drawable.ic_dialog_alert),
        )
        assertSame(configuration, NotifierInternals.configuration)
    }

    @Test
    fun permissionUtilReportsGrantedPermission() {
        shadowOf(context as Application).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
        NotifierManager.initialize(context, configuration)

        var result: Boolean? = null
        NotifierManager.getPermissionUtil().hasNotificationPermission { result = it }
        assertTrue(result == true, "Expected granted notification permission, got $result")
    }
}
