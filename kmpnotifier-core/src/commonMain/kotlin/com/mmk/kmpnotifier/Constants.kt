package com.mmk.kmpnotifier

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi

@InternalKMPNotifierApi
public object Constants {
    public const val KEY_ANDROID_FIREBASE_NOTIFICATION: String = "google.sent_time"
    public const val KEY_IOS_FIREBASE_NOTIFICATION: String = "gcm.message_id"
    public const val ACTION_NOTIFICATION_CLICK: String = "com.mmk.kmpnotifier.notification.ACTION_NOTIFICATION_CLICK"
}
