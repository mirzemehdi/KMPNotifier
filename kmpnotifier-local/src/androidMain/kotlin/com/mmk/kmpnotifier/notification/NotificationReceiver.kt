@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.notification

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.ACTION_NOTIFICATION_ACTION
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_ACTION_ID
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_NOTIFICATION_ID
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_REMOTE_INPUT
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

public class NotificationReceiver : BroadcastReceiver() {

    private companion object {
        private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }


    override fun onReceive(context: Context?, intent: Intent?) {

        val actionId = intent?.getStringExtra(EXTRA_ACTION_ID) ?: return
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        if (notificationId == 0) return


        val payloadData = mutableMapOf<String, Any?>()
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                if (ScheduledNotificationIntent.isInternalKey(key)) continue
                @Suppress("DEPRECATION")
                val value = extras.get(key)
                payloadData[key] = value
            }
        }


        when (intent.action) {
            ACTION_SCHEDULED_NOTIFICATION_FIRED -> {
                Log.d("AndroidNotificationScheduler", "Scheduled notification triggered: $notificationId")
                val appContext = context?.applicationContext
                val builder = ScheduledNotificationIntent.readNotification(intent, notificationId)
                // Prefer the live configuration; fall back to the one captured at scheduling time
                // when the alarm fires in a cold process before KMPNotifier.initialize.
                val configuration =
                    NotifierInternals.configurationOrNull() as? NotificationPlatformConfiguration.Android
                        ?: ScheduledNotificationIntent.readConfiguration(intent)
                if (appContext == null || builder == null || configuration == null) {
                    NotifierEventHub.emitAction(actionId, notificationId, payloadData)
                    return
                }

                // Nullable: goAsync() has no pending result when onReceive is invoked directly.
                val pendingResult: PendingResult? = goAsync()
                receiverScope.launch {
                    try {
                        AndroidNotificationPoster(appContext, configuration).post(builder)
                    } catch (e: Exception) {
                        Log.e("AndroidNotificationScheduler", "Failed to post scheduled notification $notificationId", e)
                    } finally {
                        NotifierEventHub.emitAction(actionId, notificationId, payloadData)
                        pendingResult?.finish()
                    }
                }
            }

            ACTION_NOTIFICATION_ACTION -> {
                Log.d("AndroidNotificationScheduler", "Action notification triggered")

                val inputResults = RemoteInput.getResultsFromIntent(intent)

                val inputText =
                    inputResults?.getCharSequence(EXTRA_REMOTE_INPUT)?.toString()

                val finalPayload = payloadData.toMutableMap()

                inputText?.let {
                    finalPayload["remote_input"] = it
                }

                // Dismiss notification
                NotifierEventHub.emitAction(actionId, notificationId, finalPayload)
            }
        }
    }
}
