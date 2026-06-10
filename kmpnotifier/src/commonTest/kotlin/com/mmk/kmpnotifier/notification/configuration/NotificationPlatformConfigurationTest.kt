package com.mmk.kmpnotifier.notification.configuration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationPlatformConfigurationTest {

    @Test
    fun androidConfigurationDefaults() {
        val config = NotificationPlatformConfiguration.Android(notificationIconResId = 1)
        assertEquals(1, config.notificationIconResId)
        assertNull(config.notificationIconColorResId)
        assertTrue(config.showPushNotification)
    }

    @Test
    fun androidNotificationChannelDataDefaults() {
        val channelData = NotificationPlatformConfiguration.Android.NotificationChannelData()
        assertEquals("DEFAULT_NOTIFICATION_CHANNEL_ID", channelData.id)
        assertEquals("General", channelData.name)
        assertEquals("", channelData.description)
        assertNull(channelData.soundUri)
    }

    @Test
    fun androidConfigurationUsesDefaultChannelData() {
        val config = NotificationPlatformConfiguration.Android(notificationIconResId = 1)
        assertEquals("DEFAULT_NOTIFICATION_CHANNEL_ID", config.notificationChannelData.id)
        assertEquals("General", config.notificationChannelData.name)
    }

    @Test
    fun iosConfigurationDefaults() {
        val config = NotificationPlatformConfiguration.Ios()
        assertTrue(config.showPushNotification)
        assertTrue(config.askNotificationPermissionOnStart)
        assertNull(config.notificationSoundName)
    }

    @Test
    fun iosConfigurationIsDataClass() {
        assertEquals(
            NotificationPlatformConfiguration.Ios(showPushNotification = false),
            NotificationPlatformConfiguration.Ios(showPushNotification = false),
        )
    }

    @Test
    fun desktopConfigurationDefaults() {
        val config = NotificationPlatformConfiguration.Desktop()
        assertTrue(config.showPushNotification)
        assertNull(config.notificationIconPath)
    }

    @Test
    fun desktopConfigurationIsDataClass() {
        assertEquals(
            NotificationPlatformConfiguration.Desktop(notificationIconPath = "/icon.png"),
            NotificationPlatformConfiguration.Desktop(notificationIconPath = "/icon.png"),
        )
    }

    @Test
    fun webConfigurationDefaults() {
        val config = NotificationPlatformConfiguration.Web()
        assertTrue(config.askNotificationPermissionOnStart)
        assertNull(config.notificationIconPath)
    }
}
