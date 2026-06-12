@file:OptIn(InternalKMPNotifierApi::class)
@file:Suppress("DEPRECATION")

package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.internal.PushEventSink
import com.mmk.kmpnotifier.local.LocalNotifications
import com.mmk.kmpnotifier.logger.Logger
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil

/**
 * Bridges the deprecated [NotifierManager] API onto the new modular API:
 * events flowing through the core event hub are forwarded to legacy
 * [NotifierManager.Listener]s.
 */
internal object NotifierManagerImpl {

    private val listeners = mutableListOf<NotifierManager.Listener>()

    private val bridge = object : KMPNotifier.Listener, PushEventSink {
        override fun onNotificationClicked(data: PayloadData) {
            listeners.toList().forEach { it.onNotificationClicked(data) }
        }

        override fun onAction(actionId: String, notificationId: Int, payload: Map<String, Any?>) {
            listeners.toList().forEach {
                it.onAction(actionId = actionId, notificationId = notificationId, payload = payload)
            }
        }

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

    private fun ensureBridge() {
        NotifierEventHub.registerInternalListener(bridge)
        NotifierEventHub.registerPushEventSink(bridge)
    }

    fun initialize(configuration: NotificationPlatformConfiguration) {
        KMPNotifier.initialize(configuration, *defaultExtensions())
        ensureBridge()
    }

    fun getConfiguration(): NotificationPlatformConfiguration = NotifierInternals.configuration

    fun getLocalNotifier(): Notifier {
        NotifierInternals.requireInitialized()
        return LocalNotifications.notifier
    }

    fun getPushNotifier(): PushNotifier {
        NotifierInternals.requireInitialized()
        return NotifierInternals.pushNotifierOrEmpty()
    }

    fun getPermissionUtil(): PermissionUtil = KMPNotifier.permissionUtil

    fun addListener(listener: NotifierManager.Listener) {
        ensureBridge()
        listeners.add(listener)
    }

    fun setListener(listener: NotifierManager.Listener?) {
        ensureBridge()
        listeners.clear()
        listener?.let { listeners.add(it) }
    }

    fun setLogger(logger: Logger) {
        KMPNotifier.setLogger(logger)
    }

    /**
     * Clears all registered listeners.
     * Intended for tests to restore a clean library state between runs.
     */
    fun reset() {
        listeners.clear()
    }
}

/**
 * Extensions installed by the deprecated single-call [NotifierManager.initialize] so existing
 * applications keep working unchanged: local notifications everywhere, Firebase push on
 * android and iOS.
 */
internal expect fun defaultExtensions(): Array<com.mmk.kmpnotifier.KMPNotifierExtension>
