package com.mmk.kmpnotifier.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mmk.kmpnotifier.notification.NotificationAction
import com.mmk.kmpnotifier.notification.NotificationImage
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.NotifierManager
import org.jetbrains.compose.resources.ExperimentalResourceApi
import kotlin.random.Random

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    var myPushNotificationToken by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("No action triggered yet") }

    LaunchedEffect(true) {
        println("LaunchedEffectApp is called")
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                myPushNotificationToken = token
                println("onNewToken: $token")
            }
        })
        myPushNotificationToken = NotifierManager.getPushNotifier().getToken() ?: ""
        println("Firebase Token: $myPushNotificationToken")
    }


    MaterialTheme {
        Column(
            Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val notifier = remember { NotifierManager.getLocalNotifier() }
            val permissionUtil = remember { NotifierManager.getPermissionUtil() }
            val scope = rememberCoroutineScope()
            var notificationId by remember { mutableStateOf(0) }

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
                    image =
                        NotificationImage.Url("https://github.com/user-attachments/assets/a0f38159-b31d-4a47-97a7-cc230e15d30b")
                }
            }) {
                Text("Send Local Notification")
            }
            Button(onClick = { notifier.removeAll() }) {
                Text("Remove all notifications")
            }

            Button(enabled = notificationId != 0, onClick = {
                notifier.remove(notificationId)
                notificationId = 0
            }) {
                Text("Remove NotificationID #$notificationId")
            }

            // New advanced notification examples
            Button(onClick = {
                notifier.notify {
                    notificationId = Random.nextInt(0, Int.MAX_VALUE)
                    title = "Notification with Actions"
                    body = "This notification has action buttons"
                    payloadData = mapOf(
                        Notifier.KEY_URL to "https://github.com/mirzemehdi/KMPNotifier/",
                        "extraKey" to "randomValue"
                    )
                    image =
                        NotificationImage.Url("https://github.com/user-attachments/assets/a0f38159-b31d-4a47-97a7-cc230e15d30b")

                    actions = listOf(
                        NotificationAction("done", "Mark Done"),
                        NotificationAction("snooze", "Snooze 5min"),
                        NotificationAction(
                            id = "CUSTOM_SNOOZE",
                            title = "Snooze",
                            allowsTextInput = true,
                            inputLabel = "Minutes"
                        )
                    )
                    scheduledAt = 0L
                }
            }) {
                Text("Notification with Actions")
            }

            Button(onClick = {

                val futureTime = 10000L // 10 seconds from now (platform will add to current time)
                notifier.notify {
                    notificationId = Random.nextInt(0, Int.MAX_VALUE)
                    title = "Notification with Actions"
                    body = "This notification has action buttons"
                    payloadData = mapOf(
                        Notifier.KEY_URL to "https://github.com/mirzemehdi/KMPNotifier/",
                        "extraKey" to "randomValue"
                    )
                    image =
                        NotificationImage.Url("https://github.com/user-attachments/assets/a0f38159-b31d-4a47-97a7-cc230e15d30b")

                    actions = listOf(
                        NotificationAction("done", "Mark Done"),
                        NotificationAction("snooze", "Snooze 5min"),
                        NotificationAction(
                            id = "CUSTOM_SNOOZE",
                            title = "Snooze",
                            allowsTextInput = true,
                            inputLabel = "Minutes"
                        )
                    )
                    scheduledAt = futureTime
                }

            }) {
                Text("Schedule Notification (10s)")
            }


            Text(
                modifier = Modifier.padding(20.dp),
                text = "FirebaseToken: $myPushNotificationToken",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Start,
            )

            Text(
                modifier = Modifier.padding(20.dp),
                text = "Action Status: $actionMessage",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )


            Button(onClick = {
                permissionUtil.askNotificationPermission {
                    println("Permission is granted")
                }
            }) {
                Text("Ask permission")
            }
        }
    }
}
