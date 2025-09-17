package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.di.KMPKoinComponent
import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import com.prinum.utils.logger.Logger
import org.koin.core.component.get

internal object NotifierManagerImpl : KMPKoinComponent() {

    private val listenersMutableList = mutableListOf<NotifierManager.Listener>()

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
        listenersMutableList.add(listener)
    }

    fun onNewToken(token: String) {
        val listenersList = listenersMutableList.toList()
        listenersList.forEach { it.onNewToken(token) }
    }

    fun onPushPayloadData(data: PayloadData) {
        val listenersList = listenersMutableList.toList()
        Logger.d(TAG, "Received Push Notification payload data")
        if (listenersList.isEmpty()) Logger.d(TAG, "There is no listener to notify onPushPayloadData")
        listenersList.forEach { it.onPayloadData(data) }
    }

    fun onPushNotification(title: String?, body: String?) {
        val listenersList = listenersMutableList.toList()
        Logger.d(TAG, "Received Push Notification notification type message")
        if (listenersList.isEmpty()) println("There is no listener to notify onPushNotification")
        listenersList.forEach { it.onPushNotification(title = title, body = body) }
    }

    fun onNotificationClicked(data: PayloadData) {
        val listenersList = listenersMutableList.toList()
        Logger.d(TAG, "Notification is clicked")
        if (listenersList.isEmpty()) Logger.d(TAG, "There is no listener to notify onPushPayloadData")
        listenersList.forEach { it.onNotificationClicked(data) }
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "NotifierFactory is not initialized. " +
                    "Please, initialize NotifierFactory by calling #initialize method"
        )
    }

}