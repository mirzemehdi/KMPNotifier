package com.mmk.kmpnotifier

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.mmk.kmpnotifier.di.ContextInitializer
import com.mmk.kmpnotifier.extensions.initialize
import com.mmk.kmpnotifier.firebase.FirebasePushNotifierImpl
import com.mmk.kmpnotifier.notification.AndroidNotifier
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.testutil.TestNotifierState
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
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
    fun contextInitializerCapturesApplicationContext() {
        ContextInitializer().create(context)
        // Initialization through the common entry point works once context is set.
        NotifierManager.initialize(configuration)
        NotifierManager.getLocalNotifier()
    }

    @Test
    fun initializeWithContextProvidesAndroidNotifier() {
        NotifierManager.initialize(context, configuration)
        assertTrue(NotifierManager.getLocalNotifier() is AndroidNotifier)
    }

    @Test
    fun initializeWithContextProvidesFirebasePushNotifier() {
        NotifierManager.initialize(context, configuration)
        assertTrue(NotifierManager.getPushNotifier() is FirebasePushNotifierImpl)
    }

    @Test
    fun initializeTwiceKeepsFirstConfiguration() {
        NotifierManager.initialize(context, configuration)
        NotifierManager.initialize(
            context,
            NotificationPlatformConfiguration.Android(notificationIconResId = android.R.drawable.ic_dialog_alert),
        )
        assertSame(configuration, NotifierManagerImpl.getConfiguration())
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
