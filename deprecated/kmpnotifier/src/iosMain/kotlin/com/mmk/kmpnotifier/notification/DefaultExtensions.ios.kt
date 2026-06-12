package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.KMPNotifierExtension
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.local.LocalNotifications
import com.mmk.kmpnotifier.push.firebase.FirebasePush

// FirebasePush is NOT installed eagerly on iOS: creating the Firebase notifier requires
// FirebaseApp.configure() to have run, and 1.x only created it on first getPushNotifier()
// access. Local-only apps using the deprecated umbrella must keep working without Firebase.
internal actual fun defaultExtensions(): Array<KMPNotifierExtension> =
    arrayOf(LocalNotifications)

@OptIn(InternalKMPNotifierApi::class)
internal actual fun installDefaultPushNotifier() {
    KMPNotifier.initialize(NotifierInternals.configuration, FirebasePush)
}
