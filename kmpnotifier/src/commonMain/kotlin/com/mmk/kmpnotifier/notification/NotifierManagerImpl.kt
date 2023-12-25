package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.di.KMPKoinComponent
import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.koin.core.component.get

internal object NotifierManagerImpl : KMPKoinComponent() {

    private val listeners = mutableListOf<NotifierManager.Listener>()

    fun initialize(configuration: NotificationPlatformConfiguration) {
        LibDependencyInitializer.initialize(configuration)
    }

    fun getLocalNotifier(): Notifier {
        requireInitialization()
        return get()
    }

    fun getPushNotifier(): PushNotifier {
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
        println("Received Push Notification payload data")
        if (listeners.size == 0) println("There is no listener to notify onPushPayloadData")
        listeners.forEach { it.onPayloadData(data) }
    }

    private fun requireInitialization() {
        if (LibDependencyInitializer.isInitialized().not()) throw IllegalStateException(
            "NotifierFactory is not initialized. " +
                    "Please, initialize NotifierFactory by calling #initialize method"
        )
    }

}