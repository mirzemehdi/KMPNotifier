package com.mmk.kmpnotifier.extensions

import android.content.Intent
import androidx.core.os.bundleOf
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl


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
    extras.keySet().forEach { key ->
        val value = extras.get(key)
        value?.let { payloadData[key] = value }
    }
    if (extras.containsKey("google.sent_time")) NotifierManagerImpl.onPushPayloadData(payloadData)
}