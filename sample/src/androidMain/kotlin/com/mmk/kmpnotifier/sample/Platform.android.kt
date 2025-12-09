package com.mmk.kmpnotifier.sample

import android.content.ContentResolver
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import androidx.core.net.toUri

actual fun onApplicationStartPlatformSpecific() {
    val customNotificationSound =
        (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + "com.mmk.kmpnotifier.sample" + "/" + R.raw.custom_notification_sound).toUri()
    NotifierManager.initialize(
        configuration = NotificationPlatformConfiguration.Android(
            notificationIconResId = R.drawable.ic_launcher_foreground,
            showPushNotification = true,
            notificationChannelDataList = listOf(NotificationPlatformConfiguration.Android.NotificationChannelData(
                    soundUri = customNotificationSound.toString()
                )
            )
        )
    )
}