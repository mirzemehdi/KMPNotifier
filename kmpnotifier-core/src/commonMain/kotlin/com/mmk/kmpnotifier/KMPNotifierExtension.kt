package com.mmk.kmpnotifier

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierRuntime

/**
 * A pluggable capability of the KMPNotifier library.
 *
 * Extensions are passed to [KMPNotifier.initialize] and installed exactly once.
 * The library ships with `LocalNotifications` (in `kmpnotifier-local`) and
 * `FirebasePush` (in `kmpnotifier-push-firebase`).
 *
 * ```
 * KMPNotifier.initialize(configuration, FirebasePush)
 * ```
 */
public interface KMPNotifierExtension {

    /**
     * Extensions that must be installed before this one.
     * They are installed automatically; users do not need to pass them explicitly.
     */
    @InternalKMPNotifierApi
    public val dependsOn: List<KMPNotifierExtension>
        get() = emptyList()

    /**
     * Called once during [KMPNotifier.initialize] (or on the first initialize call
     * that includes this extension).
     */
    @InternalKMPNotifierApi
    public fun install(runtime: NotifierRuntime)
}
