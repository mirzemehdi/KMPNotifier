@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.local

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.notification.impl.JOptionPaneNotifier
import com.mmk.kmpnotifier.notification.impl.TrayNotifier
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LocalInitializationTest {

    private val configuration = NotificationPlatformConfiguration.Desktop()

    @BeforeTest
    fun setUp() = NotifierInternals.resetForTests()

    @AfterTest
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun notifierThrowsBeforeInitialization() {
        assertFailsWith<IllegalStateException> { LocalNotifications.notifier }
        assertFailsWith<IllegalStateException> { KMPNotifier.localNotifier }
    }

    @Test
    fun initializeWithExtensionProvidesDesktopNotifier() {
        KMPNotifier.initialize(configuration, LocalNotifications)
        val notifier = KMPNotifier.localNotifier
        if (TrayNotifier.isSupported) {
            assertTrue(notifier is TrayNotifier, "Expected TrayNotifier when tray is supported")
        } else {
            assertTrue(notifier is JOptionPaneNotifier, "Expected JOptionPaneNotifier fallback")
        }
    }

    @Test
    fun lazySelfInstallWorksWithWarningWhenExtensionNotPassed() {
        val messages = mutableListOf<String>()
        KMPNotifier.setLogger { messages.add(it) }
        KMPNotifier.initialize(configuration)

        val notifier = KMPNotifier.localNotifier

        assertSame(notifier, LocalNotifications.notifier)
        assertTrue(
            messages.any { it.contains("installing lazily") },
            "Expected a lazy-install warning, got: $messages",
        )
    }
}
