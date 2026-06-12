package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.extensions.composeDesktopResourcesPath
import com.mmk.kmpnotifier.local.LocalNotifications
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import java.io.File

actual fun onApplicationStartPlatformSpecific() {
    println("Desktop app is initialized")
    KMPNotifier.initialize(
        NotificationPlatformConfiguration.Desktop(
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "ic_notification.png"
        ),
        LocalNotifications,
    )
}

