@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.internal.NotifierRuntime
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtensionInstallTest {

    private val configuration = NotificationPlatformConfiguration.Desktop()

    private class FakeExtension(
        private val name: String,
        private val installLog: MutableList<String>,
        private val dependencies: List<KMPNotifierExtension> = emptyList(),
    ) : KMPNotifierExtension {
        override val dependsOn: List<KMPNotifierExtension> get() = dependencies
        override fun install(runtime: NotifierRuntime) {
            installLog.add(name)
        }
    }

    @BeforeTest
    fun setUp() = NotifierInternals.resetForTests()

    @AfterTest
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun extensionsAreInstalledOnInitialize() {
        val log = mutableListOf<String>()
        KMPNotifier.initialize(configuration, FakeExtension("a", log))
        assertEquals(listOf("a"), log)
    }

    @Test
    fun dependenciesAreInstalledFirst() {
        val log = mutableListOf<String>()
        val base = FakeExtension("base", log)
        val dependent = FakeExtension("dependent", log, dependencies = listOf(base))

        KMPNotifier.initialize(configuration, dependent)

        assertEquals(listOf("base", "dependent"), log)
    }

    @Test
    fun extensionsInstallOnlyOnce() {
        val log = mutableListOf<String>()
        val base = FakeExtension("base", log)
        val dependent = FakeExtension("dependent", log, dependencies = listOf(base))

        KMPNotifier.initialize(configuration, base, dependent, base)
        // Second initialize call: configuration is a no-op, extensions already installed.
        KMPNotifier.initialize(configuration, base, dependent)

        assertEquals(listOf("base", "dependent"), log)
    }

    @Test
    fun laterInitializeInstallsNewExtensions() {
        val log = mutableListOf<String>()
        val first = FakeExtension("first", log)
        val second = FakeExtension("second", log)

        KMPNotifier.initialize(configuration, first)
        KMPNotifier.initialize(configuration, second)

        assertEquals(listOf("first", "second"), log)
        assertTrue(KMPNotifier.isInitialized)
    }

    @Test
    fun runtimeExposesConfigurationAndPermissionUtil() {
        var observedConfiguration: NotificationPlatformConfiguration? = null
        val extension = object : KMPNotifierExtension {
            override fun install(runtime: NotifierRuntime) {
                observedConfiguration = runtime.configuration
                runtime.permissionUtil // must resolve without throwing
            }
        }

        KMPNotifier.initialize(configuration, extension)

        assertEquals(configuration, observedConfiguration)
    }
}
