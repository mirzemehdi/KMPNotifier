package com.mmk.kmpnotifier.push.firebase

import com.mmk.kmpnotifier.firebase.FirebasePushNotifierImpl
import com.mmk.kmpnotifier.notification.PushNotifier

internal actual fun createFirebasePushNotifier(): PushNotifier = FirebasePushNotifierImpl()
