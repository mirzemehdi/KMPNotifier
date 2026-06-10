@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
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
        assertSame(configuration, NotifierInternals.configuration)
    }

    @Test
    fun localNotifierIsDesktopImplementation() {
        NotifierManager.initialize(configuration)
        val notifierClassName = NotifierManager.getLocalNotifier()::class.simpleName.orEmpty()
        assertTrue(
            notifierClassName == "TrayNotifier" || notifierClassName == "JOptionPaneNotifier",
            "Expected a desktop notifier implementation, got $notifierClassName",
        )
    }

    @Test
    fun pushNotifierBehavesAsEmptyImplementation() = runTest {
        NotifierManager.initialize(configuration)
        val pushNotifier = NotifierManager.getPushNotifier()
        assertNull(pushNotifier.getToken())
        pushNotifier.deleteMyToken()
        pushNotifier.subscribeToTopic("topic")
        pushNotifier.unSubscribeFromTopic("topic")
    }
}
