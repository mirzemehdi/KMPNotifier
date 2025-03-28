package com.mmk.kmpnotifier.extensions

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.mmk.kmpnotifier.Constants.ACTION_NOTIFICATION_CLICK
import com.mmk.kmpnotifier.Constants.KEY_ANDROID_FIREBASE_NOTIFICATION
import com.mmk.kmpnotifier.di.ContextInitializer
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration


/***
 * In order to receive notification data payload this functions needs to be called in
 * Android side in launcher Activity #onCreate and #onNewIntent methods.
 *
 * Example:
 *
 * ```
 * class MainActivity : ComponentActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         NotifierManager.onCreateOrOnNewIntent(intent)
 *         setContent {
 *             App()
 *         }
 *     }
 *
 *     override fun onNewIntent(intent: Intent?) {
 *         super.onNewIntent(intent)
 *         NotifierManager.onCreateOrOnNewIntent(intent)
 *     }
 *
 * }
 *
 * ```
 */
public fun NotifierManager.onCreateOrOnNewIntent(intent: Intent?) {
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
        NotifierManagerImpl.onPushPayloadData(payloadDataWithoutClickAction)
        NotifierManagerImpl.onPushNotificationWithPayloadData(data = payloadDataWithoutClickAction)
    }
    if (isNotificationClicked) {
        NotifierManagerImpl.onNotificationClicked(payloadData.minus(ACTION_NOTIFICATION_CLICK))
    }
}


/**
 * @param context Android application context. By default
 * using androidx-startup Context reference is passed without needing to pass one manually.
 * If you disabled androidx-startup you can use this function in android application start to pass Context reference
 * @param configuration pass android configuration
 * @see NotificationPlatformConfiguration.Android
 *
 */
public fun NotifierManager.initialize(
    context: Context,
    configuration: NotificationPlatformConfiguration
) {
    ContextInitializer().create(context)
    NotifierManagerImpl.initialize(configuration)
}

internal fun NotifierManagerImpl.shouldShowNotification(): Boolean {
    val configuration =
        NotifierManagerImpl.getConfiguration() as? NotificationPlatformConfiguration.Android
    return configuration?.showPushNotification ?: true
}
