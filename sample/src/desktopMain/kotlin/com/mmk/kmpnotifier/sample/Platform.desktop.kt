package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

actual fun onApplicationStartPlatformSpecific() {
    println("Desktop app is initialized")
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Desktop(showPushNotification = true)
    )
}