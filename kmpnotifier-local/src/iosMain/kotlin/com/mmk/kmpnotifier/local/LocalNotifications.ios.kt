@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.local

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierRuntime
import com.mmk.kmpnotifier.notification.IosNotifier
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import platform.UserNotifications.UNUserNotificationCenter

/**
 * Keeps a strong reference to the notification center delegate.
 * `UNUserNotificationCenter.delegate` is a weak Objective-C reference; without this holder
 * the Kotlin delegate object could be collected and notification callbacks silently stop.
 */
internal object IosDelegateHolder {
    var delegate: IosNotifier.NotificationDelegate? = null
}

internal actual fun createPlatformNotifier(runtime: NotifierRuntime): Notifier {
    val configuration =
        runtime.configuration as NotificationPlatformConfiguration.Ios
    val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    IosDelegateHolder.delegate = IosNotifier.NotificationDelegate().also {
        notificationCenter.delegate = it
    }
    return IosNotifier(
        permissionUtil = runtime.permissionUtil,
        notificationCenter = notificationCenter,
        iosNotificationConfiguration = configuration
    )
}
