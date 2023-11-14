package com.mmk.kmpnotifier.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mmk.kmpnotifier.notification.NotifierManager

@Composable
fun App() {
    var myPushNotificationToken by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                println("onNewToken: $token")
            }
        })
        myPushNotificationToken = NotifierManager.getPushNotifier().getToken() ?: ""
    }

    MaterialTheme {
        Column (Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Button(onClick = {
                val notifier = NotifierManager.getLocalNotifier()
                notifier.notify("Title", "bodyMessage")
            }) {
                Text("Send Local Notification")
            }
            Text(
                modifier = Modifier.padding(20.dp),
                text = "FirebaseToken: $myPushNotificationToken",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Start,
            )
        }
    }
}