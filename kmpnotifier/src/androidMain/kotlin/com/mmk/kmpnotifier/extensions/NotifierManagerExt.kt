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
public fun NotifierManager.initialize(
    context: Context,
    configuration: NotificationPlatformConfiguration
) {
    KMPNotifier.initialize(context, configuration)
    NotifierManagerImpl.initialize(configuration)
}
