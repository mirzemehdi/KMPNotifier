package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.notification.impl.JOptionPaneNotifier
import com.mmk.kmpnotifier.notification.impl.TrayNotifier

internal object DesktopNotifierFactory {
    fun getNotifier(configuration: NotificationPlatformConfiguration.Desktop): Notifier {
        return when {
            TrayNotifier.isSupported -> TrayNotifier(configuration = configuration)
            //TODO for now return JOptionPaneNotifier for not supported platforms
            else -> JOptionPaneNotifier(configuration = configuration)
        }
    }
}