package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.di.KMPKoinComponent
import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.logger.Logger
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.component.get

internal object NotifierManagerImpl : KMPKoinComponent() {
    private val listeners = mutableListOf<NotifierManager.Listener>()

    fun initialize(configuration: NotificationPlatformConfiguration) {
        LibDependencyInitializer.initialize(configuration)
    }

    fun getConfiguration(): NotificationPlatformConfiguration = get()

    fun getLocalNotifier(): Notifier {
        requireInitialization()
        return get()
    }

    fun getPushNotifier(): PushNotifier {
        requireInitialization()
        return get()
    }

    fun getPermissionUtil(): PermissionUtil {
        requireInitialization()
        return get()
    }

    fun addListener(listener: NotifierManager.Listener) {
        listeners.add(listener)
    }

    fun setListener(listener: NotifierManager.Listener?) {
        listeners.clear()
        listener?.let { listeners.add(it) }
    }

    fun onNewToken(token: String) {
        listeners.forEach { it.onNewToken(token) }
    }

    fun onPushPayloadData(data: PayloadData) {
        currentLogger.log("Received Push Notification payload data")
        if (listeners.size == 0) currentLogger.log("There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onPayloadData(data) }
    }

    fun onPushNotification(title: String?, body: String?) {
        currentLogger.log("Received Push Notification notification type message")
        if (listeners.size == 0) currentLogger.log("There is no listener to notify onPushNotification")
        listeners.forEach { it.onPushNotification(title = title, body = body) }
    }

    fun onNotificationClicked(data: PayloadData) {
        currentLogger.log("Notification is clicked")
        if (listeners.size == 0) currentLogger.log("There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onNotificationClicked(data) }
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "NotifierFactory is not initialized. " +
                    "Please, initialize NotifierFactory by calling #initialize method"
        )
    }

    fun setLogger(logger: Logger) {
        currentLogger = logger
    }
}
