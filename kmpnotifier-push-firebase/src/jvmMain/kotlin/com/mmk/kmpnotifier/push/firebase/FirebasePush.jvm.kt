@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.push.firebase

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.PushNotifier

// Firebase Cloud Messaging is not available on this platform; the shared no-op
// implementation keeps the push API usable from common code, exactly like 1.x.
internal actual fun createFirebasePushNotifier(): PushNotifier =
    NotifierInternals.pushNotifierOrEmpty()
