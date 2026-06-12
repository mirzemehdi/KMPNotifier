@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.internal

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.PayloadData

/**
 * Receives push-related events emitted into [NotifierEventHub].
 *
 * Implemented by the push module (forwarding to its public listeners) and by the
 * `kmpnotifier` compatibility artifact (forwarding to legacy `NotifierManager.Listener`s).
 */
@InternalKMPNotifierApi
public interface PushEventSink {
    public fun onNewToken(token: String) {}
    public fun onPushPayloadData(data: PayloadData) {}
    public fun onPushNotification(title: String?, body: String?) {}
    public fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {}
}

/**
 * Central fan-out point for all notification events.
 *
 * Event sources (platform delegates, broadcast receivers, messaging services) emit here.
 * Shared events (click, action) reach [KMPNotifier.Listener]s; push events reach
 * registered [PushEventSink]s.
 */
@InternalKMPNotifierApi
public object NotifierEventHub {

    // Registries use copy-on-write: emits snapshot an immutable collection, so events
    // arriving on background threads (e.g. FCM) never iterate a collection that the main
    // thread is mutating.

    /** Listeners registered through the public [KMPNotifier] API. */
    private var listeners = setOf<KMPNotifier.Listener>()

    /** Listeners registered by other library modules (e.g. the legacy bridge). */
    private var internalListeners = setOf<KMPNotifier.Listener>()

    private var pushEventSinks = setOf<PushEventSink>()

    internal fun addListener(listener: KMPNotifier.Listener) {
        listeners = listeners + listener
    }

    internal fun removeListener(listener: KMPNotifier.Listener) {
        listeners = listeners - listener
    }

    internal fun setListener(listener: KMPNotifier.Listener?) {
        listeners = if (listener != null) setOf(listener) else emptySet()
    }

    public fun registerInternalListener(listener: KMPNotifier.Listener) {
        internalListeners = internalListeners + listener
    }

    public fun unregisterInternalListener(listener: KMPNotifier.Listener) {
        internalListeners = internalListeners - listener
    }

    public fun registerPushEventSink(sink: PushEventSink) {
        pushEventSinks = pushEventSinks + sink
    }

    public fun unregisterPushEventSink(sink: PushEventSink) {
        pushEventSinks = pushEventSinks - sink
    }

    private fun allListeners(): List<KMPNotifier.Listener> =
        internalListeners.toList() + listeners.toList()

    public fun emitNotificationClicked(data: PayloadData) {
        currentLogger.log("Notification is clicked")
        val targets = allListeners()
        if (targets.isEmpty()) currentLogger.log("There is no listener to notify onNotificationClicked")
        targets.forEach { it.onNotificationClicked(data) }
    }

    public fun emitAction(actionId: String, notificationId: Int, payload: PayloadData) {
        currentLogger.log("Received action")
        val targets = allListeners()
        if (targets.isEmpty()) currentLogger.log("There is no listener to notify onAction")
        targets.forEach {
            it.onAction(actionId = actionId, notificationId = notificationId, payload = payload)
        }
    }

    public fun emitNewToken(token: String) {
        currentLogger.log("Received new push token")
        if (pushEventSinks.isEmpty()) currentLogger.log("There is no listener to notify onNewToken")
        pushEventSinks.toList().forEach { it.onNewToken(token) }
    }

    public fun emitPushPayloadData(data: PayloadData) {
        currentLogger.log("Received Push Notification payload data")
        if (pushEventSinks.isEmpty()) currentLogger.log("There is no listener to notify onPushPayloadData")
        pushEventSinks.toList().forEach { it.onPushPayloadData(data) }
    }

    public fun emitPushNotification(title: String?, body: String?) {
        currentLogger.log("Received Push Notification notification type message")
        if (pushEventSinks.isEmpty()) currentLogger.log("There is no listener to notify onPushNotification")
        pushEventSinks.toList().forEach { it.onPushNotification(title = title, body = body) }
    }

    public fun emitPushNotificationWithPayloadData(
        title: String? = null,
        body: String? = null,
        data: PayloadData,
    ) {
        currentLogger.log("Received Push Notification")
        if (pushEventSinks.isEmpty()) currentLogger.log("There is no listener to notify onPushNotificationWithPayloadData")
        pushEventSinks.toList().forEach {
            it.onPushNotificationWithPayloadData(title = title, body = body, data = data)
        }
    }

    internal fun reset() {
        listeners = emptySet()
        internalListeners = emptySet()
        pushEventSinks = emptySet()
    }
}
