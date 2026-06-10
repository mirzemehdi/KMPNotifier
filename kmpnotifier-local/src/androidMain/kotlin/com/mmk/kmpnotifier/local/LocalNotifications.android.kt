@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.local

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.internal.NotifierRuntime
import com.mmk.kmpnotifier.internal.requireApplicationContext
import com.mmk.kmpnotifier.notification.AndroidNotifier
import com.mmk.kmpnotifier.notification.NotificationChannelFactory
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

internal actual fun createPlatformNotifier(runtime: NotifierRuntime): Notifier {
    val configuration =
        runtime.configuration as NotificationPlatformConfiguration.Android
    val context = NotifierInternals.requireApplicationContext()
    return AndroidNotifier(
        context = context,
        androidNotificationConfiguration = configuration,
        notificationChannelFactory = NotificationChannelFactory(
            context = context,
            channelData = configuration.notificationChannelData
        ),
        permissionUtil = runtime.permissionUtil
    )
}
