@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.push.firebase

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.KMPNotifierExtension
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.internal.NotifierRuntime
import com.mmk.kmpnotifier.internal.PushEventSink
import com.mmk.kmpnotifier.local.LocalNotifications
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.push.PushListener

/**
 * Firebase Cloud Messaging push capability of KMPNotifier.
 *
 * Install it by passing it to [KMPNotifier.initialize] — local notifications are
 * installed automatically as a dependency:
 * ```
 * KMPNotifier.initialize(configuration, FirebasePush)
 * ```
 * The push notifier is then available through [notifier] (or `KMPNotifier.firebasePushNotifier`),
 * and push events through [addListener].
 *
 * Firebase delivers push notifications on android and iOS. On desktop and web the
 * extension installs a no-op notifier (token is null, subscriptions do nothing), so
 * shared code can use the same API on every target — exactly like 1.x.
 */
public object FirebasePush : KMPNotifierExtension {

    // Copy-on-write set: token events arrive on background threads; set semantics keep
    // duplicate registration of the same listener from double-firing (matches KMPNotifier.addListener).
    private var listeners = setOf<PushListener>()

    internal val eventSink: PushEventSink = object : PushEventSink {
        override fun onNewToken(token: String) {
            listeners.toList().forEach { it.onNewToken(token) }
        }

        override fun onPushPayloadData(data: PayloadData) {
            listeners.toList().forEach { it.onPayloadData(data) }
        }

        override fun onPushNotification(title: String?, body: String?) {
            listeners.toList().forEach { it.onPushNotification(title = title, body = body) }
        }

        override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
            listeners.toList().forEach {
                it.onPushNotificationWithPayloadData(title = title, body = body, data = data)
            }
        }
    }

    /**
     * The Firebase-backed push notifier.
     *
     * @throws IllegalStateException if the library is not initialized, or if [FirebasePush]
     * was not passed to [KMPNotifier.initialize]
     */
    public val notifier: PushNotifier
        get() {
            NotifierInternals.requireInitialized()
            return NotifierInternals.pushNotifierOrNull() ?: throw IllegalStateException(
                "Push is not initialized. " +
                        "Pass FirebasePush to KMPNotifier.initialize(configuration, FirebasePush)."
            )
        }

    /** Adds a listener for push events. Safe to call before [KMPNotifier.initialize]. */
    public fun addListener(listener: PushListener) {
        NotifierEventHub.registerPushEventSink(eventSink)
        listeners = listeners + listener
    }

    /** Removes a previously added listener. */
    public fun removeListener(listener: PushListener) {
        listeners = listeners - listener
    }

    /** Replaces all push listeners with the given one. Pass null to remove all listeners. */
    public fun setListener(listener: PushListener?) {
        NotifierEventHub.registerPushEventSink(eventSink)
        listeners = if (listener != null) setOf(listener) else emptySet()
    }

    @InternalKMPNotifierApi
    override val dependsOn: List<KMPNotifierExtension>
        get() = listOf(LocalNotifications)

    @InternalKMPNotifierApi
    override fun install(runtime: NotifierRuntime) {
        NotifierEventHub.registerPushEventSink(eventSink)
        if (NotifierInternals.pushNotifierOrNull() == null) {
            NotifierInternals.registerPushNotifier(createFirebasePushNotifier())
        }
    }
}

/**
 * The Firebase-backed push notifier.
 * Kotlin-side sugar for [FirebasePush.notifier]
 * (from Swift use `FirebasePush.shared.notifier`).
 */
public val KMPNotifier.firebasePushNotifier: PushNotifier
    get() = FirebasePush.notifier

/** Creates the platform's Firebase push notifier. */
internal expect fun createFirebasePushNotifier(): PushNotifier
