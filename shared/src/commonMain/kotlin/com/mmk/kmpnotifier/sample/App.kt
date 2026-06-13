package com.mmk.kmpnotifier.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.local.localNotifier
import com.mmk.kmpnotifier.notification.NotificationAction
import com.mmk.kmpnotifier.notification.NotificationImage
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.push.PushListener
import com.mmk.kmpnotifier.push.firebase.addPushListener
import com.mmk.kmpnotifier.push.firebase.firebasePushNotifier
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val SAMPLE_TOPIC = "new_users"

@Composable
fun App() {
    var pushToken by remember { mutableStateOf("") }
    var lastSharedEvent by remember { mutableStateOf("none yet") }
    var lastPushEvent by remember { mutableStateOf("none yet") }
    var permissionStatus by remember { mutableStateOf("unknown") }
    var topicStatus by remember { mutableStateOf("not subscribed") }

    LaunchedEffect(true) {
        // Shared events — clicks and action buttons, fired for BOTH local and push notifications.
        KMPNotifier.addListener(object : KMPNotifier.Listener {
            override fun onNotificationClicked(data: PayloadData) {
                lastSharedEvent = "clicked: $data"
            }

            override fun onAction(actionId: String, notificationId: Int, payload: PayloadData) {
                lastSharedEvent = "action '$actionId' on #$notificationId"
            }
        })
        // Push events — tokens and payloads (no-op mock delivers nothing on desktop/web).
        KMPNotifier.addPushListener(object : PushListener {
            override fun onNewToken(token: String) {
                pushToken = token
            }

            override fun onPushNotification(title: String?, body: String?) {
                lastPushEvent = "notification: $title / $body"
            }

            override fun onPayloadData(data: PayloadData) {
                lastPushEvent = "payload: $data"
            }

            override fun onPushNotificationWithPayloadData(title: String?, body: String?, data: PayloadData) {
                lastPushEvent = "notification+payload: $title — $data"
            }
        })
        pushToken = KMPNotifier.firebasePushNotifier.getToken() ?: "(no token on this platform)"
    }

    MaterialTheme {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val notifier = remember { KMPNotifier.localNotifier }
            val permissionUtil = remember { KMPNotifier.permissionUtil }
            val scope = rememberCoroutineScope()
            var notificationId by remember { mutableStateOf(0) }
            var scheduledId by remember { mutableStateOf(0) }

            Text("Local notifications", style = MaterialTheme.typography.h6)

            Button(onClick = {
                notificationId = Random.nextInt(0, Int.MAX_VALUE)
                notifier.notify {
                    id = notificationId
                    title = "Title from KMPNotifier"
                    body = "Body message from KMPNotifier"
                    payloadData = mapOf(
                        Notifier.KEY_URL to "https://github.com/mirzemehdi/KMPNotifier/",
                        "extraKey" to "randomValue"
                    )
                    image = NotificationImage.Url("https://github.com/user-attachments/assets/a0f38159-b31d-4a47-97a7-cc230e15d30b")
                }
            }) { Text("Send local notification (image + payload)") }

            Button(onClick = {
                notificationId = Random.nextInt(0, Int.MAX_VALUE)
                notifier.notify(
                    id = notificationId,
                    title = "Plain notification #$notificationId",
                    body = "Sent with explicit id"
                )
            }) { Text("Send with explicit id") }

            Button(onClick = {
                notificationId = Random.nextInt(0, Int.MAX_VALUE)
                notifier.notify {
                    id = notificationId
                    title = "Notification with actions"
                    body = "Tap an action button"
                    actions = listOf(
                        NotificationAction("done", "Mark Done"),
                        NotificationAction(
                            id = "CUSTOM_SNOOZE",
                            title = "Snooze",
                            allowsTextInput = true,
                            inputLabel = "Minutes"
                        )
                    )
                }
            }) { Text("Notification with action buttons") }

            Button(onClick = {
                scheduledId = Random.nextInt(0, Int.MAX_VALUE)
                notifier.notify {
                    id = scheduledId
                    title = "Scheduled notification"
                    body = "Fired ~10s after scheduling"
                    scheduledAt = 10_000L // relative ms from now
                }
            }) { Text("Schedule notification (10s)") }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = scheduledId != 0, onClick = {
                    notifier.remove(scheduledId) // also cancels the pending alarm
                    scheduledId = 0
                }) { Text("Cancel scheduled") }

                Button(enabled = notificationId != 0, onClick = {
                    notifier.remove(notificationId)
                    notificationId = 0
                }) { Text("Remove last") }

                Button(onClick = { notifier.removeAll() }) { Text("Remove all") }
            }

            Divider(Modifier.padding(vertical = 8.dp))
            Text("Push notifications", style = MaterialTheme.typography.h6)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Token: $pushToken",
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Start,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch {
                        KMPNotifier.firebasePushNotifier.subscribeToTopic(SAMPLE_TOPIC)
                        topicStatus = "subscribed to $SAMPLE_TOPIC"
                    }
                }) { Text("Subscribe topic") }

                Button(onClick = {
                    scope.launch {
                        KMPNotifier.firebasePushNotifier.unSubscribeFromTopic(SAMPLE_TOPIC)
                        topicStatus = "unsubscribed from $SAMPLE_TOPIC"
                    }
                }) { Text("Unsubscribe") }
            }
            Text("Topic: $topicStatus", style = MaterialTheme.typography.caption)

            Button(onClick = {
                scope.launch {
                    KMPNotifier.firebasePushNotifier.deleteMyToken()
                    pushToken = KMPNotifier.firebasePushNotifier.getToken() ?: "(deleted)"
                }
            }) { Text("Delete my token") }

            Divider(Modifier.padding(vertical = 8.dp))
            Text("Permissions", style = MaterialTheme.typography.h6)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    permissionUtil.hasNotificationPermission { granted ->
                        permissionStatus = if (granted) "granted" else "denied"
                    }
                }) { Text("Check permission") }

                Button(onClick = {
                    permissionUtil.askNotificationPermission { granted ->
                        permissionStatus = if (granted) "granted" else "denied"
                    }
                }) { Text("Ask permission") }
            }
            Text("Permission: $permissionStatus", style = MaterialTheme.typography.caption)

            Divider(Modifier.padding(vertical = 8.dp))
            Text("Events", style = MaterialTheme.typography.h6)
            Text("Shared (click/action): $lastSharedEvent", style = MaterialTheme.typography.body2, textAlign = TextAlign.Center)
            Text("Push: $lastPushEvent", style = MaterialTheme.typography.body2, textAlign = TextAlign.Center)
        }
    }
}
