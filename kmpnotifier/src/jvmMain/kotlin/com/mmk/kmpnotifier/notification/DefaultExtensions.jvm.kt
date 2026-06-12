package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.KMPNotifierExtension
import com.mmk.kmpnotifier.local.LocalNotifications
import com.mmk.kmpnotifier.push.firebase.FirebasePush

internal actual fun defaultExtensions(): Array<KMPNotifierExtension> =
    arrayOf(LocalNotifications, FirebasePush)
