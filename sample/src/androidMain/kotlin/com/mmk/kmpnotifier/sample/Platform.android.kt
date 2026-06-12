package com.mmk.kmpnotifier.sample

import android.content.ContentResolver
import android.net.Uri
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.sample.shared.R
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.push.firebase.FirebasePush

actual fun onApplicationStartPlatformSpecific() {
    val customNotificationSound =
        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + "com.mmk.kmpnotifier.sample" + "/" + R.raw.custom_notification_sound)
    KMPNotifier.initialize(
        configuration = NotificationPlatformConfiguration.Android(
            notificationIconResId = R.drawable.ic_launcher_foreground,
            showPushNotification = true,
            notificationChannelData = NotificationPlatformConfiguration.Android.NotificationChannelData(
                soundUri = customNotificationSound.toString()
            )
        ),
        FirebasePush,
    )
}

