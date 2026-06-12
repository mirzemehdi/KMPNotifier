@file:Suppress("UnusedReceiverParameter")
@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.extensions

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.mmk.kmpnotifier.Constants.ACTION_NOTIFICATION_CLICK
import com.mmk.kmpnotifier.Constants.KEY_ANDROID_FIREBASE_NOTIFICATION
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.KMPNotifierExtension
import com.mmk.kmpnotifier.di.ContextInitializer
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration


/***
 * In order to receive notification data payload this function needs to be called in
 * Android side in launcher Activity #onCreate and #onNewIntent methods.
 *
 * Example:
 *
 * ```
 * class MainActivity : ComponentActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         KMPNotifier.onCreateOrOnNewIntent(intent)
 *         setContent {
 *             App()
 *         }
 *     }
 *
 *     override fun onNewIntent(intent: Intent?) {
 *         super.onNewIntent(intent)
 *         KMPNotifier.onCreateOrOnNewIntent(intent)
 *     }
 *
 * }
 *
 * ```
 */
public fun KMPNotifier.onCreateOrOnNewIntent(intent: Intent?) {
    if (intent == null) return
    val extras = intent.extras ?: bundleOf()
    val payloadData = mutableMapOf<String, Any>()

    val isNotificationClicked =
        extras.containsKey(ACTION_NOTIFICATION_CLICK)
                || extras.containsKey(KEY_ANDROID_FIREBASE_NOTIFICATION)
                || payloadData.containsKey(ACTION_NOTIFICATION_CLICK)

    extras.keySet().forEach { key ->
        val value = extras.get(key)
        value?.let { payloadData[key] = it }
    }

    if (extras.containsKey(KEY_ANDROID_FIREBASE_NOTIFICATION)) {
        val payloadDataWithoutClickAction = payloadData.minus(ACTION_NOTIFICATION_CLICK)
        NotifierEventHub.emitPushPayloadData(payloadDataWithoutClickAction)
        NotifierEventHub.emitPushNotificationWithPayloadData(data = payloadDataWithoutClickAction)
    }
    if (isNotificationClicked) {
        NotifierEventHub.emitNotificationClicked(payloadData.minus(ACTION_NOTIFICATION_CLICK))
    }
}


/**
 * @param context Android application context. By default
 * using androidx-startup Context reference is passed without needing to pass one manually.
 * If you disabled androidx-startup you can use this function in android application start to pass Context reference
 * @param configuration pass android configuration
 * @param extensions capabilities to install, e.g. `LocalNotifications`, `FirebasePush`
 * @see NotificationPlatformConfiguration.Android
 *
 */
public fun KMPNotifier.initialize(
    context: Context,
    configuration: NotificationPlatformConfiguration,
    vararg extensions: KMPNotifierExtension,
) {
    ContextInitializer().create(context)
    KMPNotifier.initialize(configuration, *extensions)
}
