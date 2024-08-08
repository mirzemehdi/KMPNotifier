package com.mmk.kmpnotifier.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.extensions.notificationManager

internal class NotificationChannelFactory(
    private val context: Context,
    private val channelData: NotificationPlatformConfiguration.Android.NotificationChannelData,
) {

    fun createChannels() {
        val notificationManager = context.notificationManager ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            channelData.id,
            channelData.name,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            this.description = channelData.description
            enableLights(true)
            setSound(channelData.soundUri)
        }

        notificationManager.createNotificationChannel(channel)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun NotificationChannel.setSound(soundUri: String?) = kotlin.runCatching {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val uri = soundUri?.let { Uri.parse(soundUri) }
        if (uri != null && audioAttributes != null) {
            setSound(uri, audioAttributes)
        }
    }

}