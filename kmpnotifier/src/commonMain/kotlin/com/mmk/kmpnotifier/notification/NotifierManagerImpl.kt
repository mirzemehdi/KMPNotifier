package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.di.KMPKoinComponent
import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import com.prinum.utils.logger.Logger
import org.koin.core.component.get

internal object NotifierManagerImpl : KMPKoinComponent() {
    private val listeners = mutableListOf<NotifierManager.Listener>()

    private const val TAG = "NotifierManagerImpl"
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

    fun onNewToken(token: String) {
        listeners.forEach { it.onNewToken(token) }
    }

    fun onPushPayloadData(data: PayloadData) {
        Logger.d(TAG, "Received Push Notification payload data")
        if (listeners.size == 0) Logger.d(TAG, "There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onPayloadData(data) }
    }

    fun onPushNotification(title: String?, body: String?) {
        Logger.d(TAG, "Received Push Notification notification type message")
        if (listeners.size == 0) println("There is no listener to notify onPushNotification")
        listeners.forEach { it.onPushNotification(title = title, body = body) }
    }

    fun onNotificationClicked(data: PayloadData) {
        Logger.d(TAG, "Notification is clicked")
        if (listeners.size == 0) Logger.d(TAG, "There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onNotificationClicked(data) }
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "NotifierFactory is not initialized. " +
                    "Please, initialize NotifierFactory by calling #initialize method"
        )
    }

}