package com.mmk.kmpnotifier.sample

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

actual fun onApplicationStartPlatformSpecific() {
    NotifierManager.initialize(
        configuration = NotificationPlatformConfiguration.Android(
            notificationIconResId = R.drawable.ic_launcher_foreground,
        )
    )
}