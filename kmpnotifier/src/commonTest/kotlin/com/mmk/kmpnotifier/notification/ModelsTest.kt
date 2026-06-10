package com.mmk.kmpnotifier.notification

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class ModelsTest {

    @Test
    fun notificationActionDefaults() {
        val action = NotificationAction(id = "reply", title = "Reply")
        assertEquals("reply", action.id)
        assertEquals("Reply", action.title)
        assertFalse(action.allowsTextInput)
        assertNull(action.inputLabel)
    }

    @Test
    fun notificationActionEquality() {
        assertEquals(
            NotificationAction(id = "a", title = "T", allowsTextInput = true, inputLabel = "L"),
            NotificationAction(id = "a", title = "T", allowsTextInput = true, inputLabel = "L"),
        )
    }

    @Test
    fun notificationImageUrlEquality() {
        assertEquals(NotificationImage.Url("https://x"), NotificationImage.Url("https://x"))
        assertNotEquals<NotificationImage>(NotificationImage.Url("https://x"), NotificationImage.File("https://x"))
    }

    @Test
    fun notificationImageFileEquality() {
        assertEquals(NotificationImage.File("/a/b.png"), NotificationImage.File("/a/b.png"))
    }

    @Test
    fun notifierKeyUrlDefault() {
        assertEquals("URL", Notifier.KEY_URL)
    }
}
