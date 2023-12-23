package com.mmk.kmpnotifier.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.AndroidPermissionUtil
import com.mmk.kmpnotifier.permission.permissionUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()
        onPushNotificationData(intent.extras)
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
            )
        )
        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        onPushNotificationData(intent?.extras)
    }

    private fun onPushNotificationData(extras: Bundle?){
        extras?.keySet()?.forEach {key->
            println("PushNotification Data key: $key, value: ${extras.get(key)}")
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}