@file:Suppress("DEPRECATION")

package com.mmk.kmpnotifier.extensions

import android.content.Context
import android.content.Intent
import com.mmk.kmpnotifier.KMPNotifier
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
@Deprecated(
    message = "Use KMPNotifier.onCreateOrOnNewIntent(intent). See MIGRATION.md.",
    replaceWith = ReplaceWith(
        "KMPNotifier.onCreateOrOnNewIntent(intent)",
        "com.mmk.kmpnotifier.KMPNotifier",
        "com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent",
    ),
)
public fun NotifierManager.onCreateOrOnNewIntent(intent: Intent?) {
    KMPNotifier.onCreateOrOnNewIntent(intent)
}


/**
 * @param context Android application context. By default
 * using androidx-startup Context reference is passed without needing to pass one manually.
 * If you disabled androidx-startup you can use this function in android application start to pass Context reference
 * @param configuration pass android configuration
 * @see NotificationPlatformConfiguration.Android
 *
 */
@Deprecated(
    message = "Use KMPNotifier.initialize(context, configuration, FirebasePush) for push or " +
            "KMPNotifier.initialize(context, configuration, LocalNotifications) for local-only usage. " +
            "See MIGRATION.md.",
)
public fun NotifierManager.initialize(
    context: Context,
    configuration: NotificationPlatformConfiguration
) {
    KMPNotifier.initialize(context, configuration)
    NotifierManagerImpl.initialize(configuration)
}
