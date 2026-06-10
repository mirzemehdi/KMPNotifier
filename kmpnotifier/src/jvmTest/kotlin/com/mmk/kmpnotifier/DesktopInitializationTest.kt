package com.mmk.kmpnotifier

import com.mmk.kmpnotifier.notification.EmptyPushNotifierImpl
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.notification.impl.JOptionPaneNotifier
import com.mmk.kmpnotifier.notification.impl.TrayNotifier
import com.mmk.kmpnotifier.testutil.TestNotifierState
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DesktopInitializationTest {

    private val configuration = NotificationPlatformConfiguration.Desktop(
        showPushNotification = true,
        notificationIconPath = null,
    )

    @BeforeTest
    fun setUp() = TestNotifierState.resetAll()

    @AfterTest
    fun tearDown() = TestNotifierState.resetAll()

    @Test
    fun initializeSucceedsOnDesktop() {
        NotifierManager.initialize(configuration)
        // No exception and getters work afterwards.
        NotifierManager.getLocalNotifier()
        NotifierManager.getPushNotifier()
        NotifierManager.getPermissionUtil()
    }

    @Test
    fun initializeTwiceKeepsFirstConfiguration() {
        NotifierManager.initialize(configuration)
        NotifierManager.initialize(
            NotificationPlatformConfiguration.Desktop(notificationIconPath = "/other/icon.png")
        )
        assertSame(configuration, NotifierManagerImpl.getConfiguration())
    }

    @Test
    fun localNotifierMatchesTraySupport() {
        NotifierManager.initialize(configuration)
        val notifier = NotifierManager.getLocalNotifier()
        if (TrayNotifier.isSupported) {
            assertTrue(notifier is TrayNotifier, "Expected TrayNotifier when tray is supported")
        } else {
            assertTrue(notifier is JOptionPaneNotifier, "Expected JOptionPaneNotifier fallback")
        }
    }

    @Test
    fun pushNotifierBehavesAsEmptyImplementation() = runTest {
        NotifierManager.initialize(configuration)
        val pushNotifier = NotifierManager.getPushNotifier()
        assertTrue(pushNotifier is EmptyPushNotifierImpl)
        assertNull(pushNotifier.getToken())
        pushNotifier.deleteMyToken()
        pushNotifier.subscribeToTopic("topic")
        pushNotifier.unSubscribeFromTopic("topic")
    }
}
