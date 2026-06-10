package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.local.LocalNotifications
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

actual fun onApplicationStartPlatformSpecific() {
    println("Web app is initialized")
    KMPNotifier.initialize(
        NotificationPlatformConfiguration.Web(
            askNotificationPermissionOnStart = true,
            notificationIconPath = null
        ),
        LocalNotifications,
    )
}

