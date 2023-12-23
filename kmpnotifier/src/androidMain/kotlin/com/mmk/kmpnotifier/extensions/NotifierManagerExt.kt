package com.mmk.kmpnotifier.extensions

import android.content.Intent
import androidx.core.os.bundleOf
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.NotifierManagerImpl

public fun NotifierManager.onCreateOrOnNewIntent(intent: Intent?) {
    if (intent == null) return
    val extras = intent.extras ?: bundleOf()
    if (extras.containsKey("google.sent_time").not()) return

    val payloadData = mutableMapOf<String, Any>()
    extras.keySet().forEach { key ->
        val value = extras.get(key)
        value?.let { payloadData[key] = value }
    }
    NotifierManagerImpl.onPushPayloadData(payloadData)
}