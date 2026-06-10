@file:Suppress("UnusedReceiverParameter")

package com.mmk.kmpnotifier.push.firebase

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.push.PushListener

/**
 * Adds a listener for push events (token updates, push payloads).
 * Sugar for [FirebasePush.addListener] so call sites read `KMPNotifier.addPushListener(...)`
 * (Swift: `KMPNotifier.shared.addPushListener(listener:)`).
 */
public fun KMPNotifier.addPushListener(listener: PushListener) {
    FirebasePush.addListener(listener)
}

/** Removes a previously added push listener. Sugar for [FirebasePush.removeListener]. */
public fun KMPNotifier.removePushListener(listener: PushListener) {
    FirebasePush.removeListener(listener)
}

/** Replaces all push listeners with the given one (null removes all). Sugar for [FirebasePush.setListener]. */
public fun KMPNotifier.setPushListener(listener: PushListener?) {
    FirebasePush.setListener(listener)
}
