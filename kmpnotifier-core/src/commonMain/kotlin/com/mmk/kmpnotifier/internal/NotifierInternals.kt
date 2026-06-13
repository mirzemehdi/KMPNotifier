@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.internal

import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.logger.EmptyLogger
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.EmptyPushNotifierImpl
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil

/**
 * Cross-module state and services of the KMPNotifier library.
 *
 * Holds the registries that other library modules plug into:
 * the local notifier (registered by `kmpnotifier-local`) and the push notifier
 * (registered by `kmpnotifier-push-firebase`).
 */
@InternalKMPNotifierApi
public object NotifierInternals {

    /**
     * The installed local notifier. Typed as [Any] because the `Notifier` interface
     * lives in `kmpnotifier-local`, which depends on this module.
     */
    private var localNotifier: Any? = null

    private var pushNotifier: PushNotifier? = null

    private val emptyPushNotifier: PushNotifier by lazy { EmptyPushNotifierImpl() }

    private val installedExtensions = mutableSetOf<com.mmk.kmpnotifier.KMPNotifierExtension>()

    public fun requireInitialized() {
        if (!LibDependencyInitializer.isInitialized()) throw IllegalStateException(
            "KMPNotifier is not initialized. " +
                    "Please, initialize it by calling KMPNotifier.initialize method " +
                    "(or the deprecated NotifierManager.initialize) on application start."
        )
    }

    public val configuration: NotificationPlatformConfiguration
        get() {
            requireInitialized()
            return requireNotNull(LibDependencyInitializer.dependencies).configuration
        }

    public fun configurationOrNull(): NotificationPlatformConfiguration? =
        LibDependencyInitializer.dependencies?.configuration

    public val permissionUtil: PermissionUtil
        get() {
            requireInitialized()
            return requireNotNull(LibDependencyInitializer.dependencies).permissionUtil
        }

    /** Builds the runtime handed to extensions, e.g. for lazy self-installation. */
    public fun runtime(): NotifierRuntime {
        requireInitialized()
        return object : NotifierRuntime {
            override val configuration: NotificationPlatformConfiguration get() = NotifierInternals.configuration
            override val permissionUtil: PermissionUtil get() = NotifierInternals.permissionUtil
        }
    }

    public fun registerLocalNotifier(notifier: Any) {
        localNotifier = notifier
    }

    public fun localNotifierOrNull(): Any? = localNotifier

    public fun registerPushNotifier(notifier: PushNotifier) {
        pushNotifier = notifier
    }

    public fun pushNotifierOrNull(): PushNotifier? = pushNotifier

    /** Registered push notifier, or the no-op implementation used on platforms without push. */
    public fun pushNotifierOrEmpty(): PushNotifier = pushNotifier ?: emptyPushNotifier

    internal fun installExtensions(extensions: List<com.mmk.kmpnotifier.KMPNotifierExtension>) {
        val runtime = runtime()
        fun installRecursively(extension: com.mmk.kmpnotifier.KMPNotifierExtension, visiting: MutableSet<com.mmk.kmpnotifier.KMPNotifierExtension>) {
            if (extension in installedExtensions) return
            check(extension in visiting == false) {
                "Extension dependency cycle detected involving $extension"
            }
            visiting.add(extension)
            extension.dependsOn.forEach { installRecursively(it, visiting) }
            try {
                extension.install(runtime)
            } catch (e: Throwable) {
                throw IllegalStateException("Failed to install KMPNotifier extension $extension", e)
            }
            installedExtensions.add(extension)
        }
        extensions.forEach { installRecursively(it, mutableSetOf()) }
    }

    /**
     * Restores the library to a clean, uninitialized state.
     * Intended for tests only.
     */
    public fun resetForTests() {
        LibDependencyInitializer.reset()
        NotifierEventHub.reset()
        localNotifier = null
        pushNotifier = null
        installedExtensions.clear()
        currentLogger = EmptyLogger
    }

}
