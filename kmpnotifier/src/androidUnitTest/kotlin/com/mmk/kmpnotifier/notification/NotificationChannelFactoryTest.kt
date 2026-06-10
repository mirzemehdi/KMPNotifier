package com.mmk.kmpnotifier.notification

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NotificationChannelFactoryTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val notificationManager: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Test
    fun createChannelsCreatesChannelWithGivenData() {
        val channelData = NotificationPlatformConfiguration.Android.NotificationChannelData(
            id = "test_channel_id",
            name = "Test Channel",
            description = "Test description",
        )

        NotificationChannelFactory(context, channelData).createChannels()

        val channel = notificationManager.getNotificationChannel("test_channel_id")
        assertNotNull(channel)
        assertEquals("Test Channel", channel.name)
        assertEquals("Test description", channel.description)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
    }

    @Test
    fun createChannelsUsesDefaultChannelData() {
        val channelData = NotificationPlatformConfiguration.Android.NotificationChannelData()

        NotificationChannelFactory(context, channelData).createChannels()

        val channel = notificationManager.getNotificationChannel("DEFAULT_NOTIFICATION_CHANNEL_ID")
        assertNotNull(channel)
        assertEquals("General", channel.name)
    }

    @Test
    fun createChannelsAppliesCustomSound() {
        val soundUri = "content://test/sound"
        val channelData = NotificationPlatformConfiguration.Android.NotificationChannelData(
            id = "sound_channel",
            soundUri = soundUri,
        )

        NotificationChannelFactory(context, channelData).createChannels()

        val channel = notificationManager.getNotificationChannel("sound_channel")
        assertNotNull(channel)
        assertEquals(Uri.parse(soundUri), channel.sound)
    }
}
