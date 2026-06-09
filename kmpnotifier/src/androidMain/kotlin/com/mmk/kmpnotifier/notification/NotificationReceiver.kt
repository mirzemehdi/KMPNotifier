package com.mmk.kmpnotifier.notification

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.ACTION_NOTIFICATION_ACTION
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_ACTION_ID
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_NOTIFICATION_ID
import com.mmk.kmpnotifier.notification.AndroidNotifier.Companion.EXTRA_REMOTE_INPUT

public class NotificationReceiver : BroadcastReceiver() {

    private val notifierManager by lazy { NotifierManagerImpl }



    override fun onReceive(context: Context?, intent: Intent?) {

        val actionId = intent?.getStringExtra(EXTRA_ACTION_ID) ?: return
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        if (notificationId == 0) return


        val payloadData = mutableMapOf<String, Any?>()
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
               payloadData[key] = value
            }
        }


        when (intent.action) {
            ACTION_SCHEDULED_NOTIFICATION_FIRED -> {
                // For now, we'll handle this as immediate notification
                Log.d("AndroidNotificationScheduler", "Scheduled notification triggered: $notificationId")
                notifierManager.onAction(actionId, notificationId, payloadData)
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
                notifierManager.onAction(actionId, notificationId, finalPayload)
            }
        }
    }
}