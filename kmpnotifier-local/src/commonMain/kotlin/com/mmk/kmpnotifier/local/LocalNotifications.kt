@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.local

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.KMPNotifierExtension
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.internal.NotifierRuntime
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.Notifier

/**
 * Local notification capability of KMPNotifier.
 *
 * Install it by passing it to [KMPNotifier.initialize]:
 * ```
 * KMPNotifier.initialize(configuration, LocalNotifications)
 * ```
 * The notifier is then available through [notifier] (or `KMPNotifier.localNotifier`).
 */
public object LocalNotifications : KMPNotifierExtension {

    /**
     * The local notifier of the current platform.
     *
     * If [KMPNotifier.initialize] was called without this extension, accessing this property
     * installs it lazily (a warning is logged). Prefer passing [LocalNotifications] to
     * `initialize` — on iOS the notification delegate must be installed at application start
     * to receive cold-start notification clicks.
     *
     * @throws IllegalStateException if [KMPNotifier.initialize] has not been called
     */
    public val notifier: Notifier
        get() {
            NotifierInternals.requireInitialized()
            val existing = NotifierInternals.localNotifierOrNull() as? Notifier
            if (existing != null) return existing
            currentLogger.log(
                "LocalNotifications was not passed to KMPNotifier.initialize; installing lazily. " +
                        "Pass LocalNotifications (or FirebasePush) to initialize for reliable behavior."
            )
            return installNotifier(NotifierInternals.runtime())
        }

    @InternalKMPNotifierApi
    override fun install(runtime: NotifierRuntime) {
        installNotifier(runtime)
    }

    private fun installNotifier(runtime: NotifierRuntime): Notifier {
        (NotifierInternals.localNotifierOrNull() as? Notifier)?.let { return it }
        val notifier = createPlatformNotifier(runtime)
        NotifierInternals.registerLocalNotifier(notifier)
        return notifier
    }
}

/**
 * The local notifier of the current platform.
 * Kotlin-side sugar for [LocalNotifications.notifier]
 * (from Swift use `LocalNotifications.shared.notifier`).
 */
public val KMPNotifier.localNotifier: Notifier
    get() = LocalNotifications.notifier

/** Creates the platform's local notifier. The iOS actual also installs the notification delegate. */
internal expect fun createPlatformNotifier(runtime: NotifierRuntime): Notifier
