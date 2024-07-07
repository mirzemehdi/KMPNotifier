package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.extensions.composeDesktopResourcesPath
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import java.io.File

actual fun onApplicationStartPlatformSpecific() {
    println("Desktop app is initialized")
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = true,
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "ic_notification.png"
        )
    )
}