@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.local

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierRuntime
import com.mmk.kmpnotifier.notification.DesktopNotifierFactory
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

internal actual fun createPlatformNotifier(runtime: NotifierRuntime): Notifier {
    val configuration =
        runtime.configuration as NotificationPlatformConfiguration.Desktop
    return DesktopNotifierFactory.getNotifier(configuration = configuration)
}
