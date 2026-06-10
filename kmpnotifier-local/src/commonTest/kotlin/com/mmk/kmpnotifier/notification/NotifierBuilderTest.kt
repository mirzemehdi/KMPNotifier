package com.mmk.kmpnotifier.notification

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotifierBuilderTest {

    @Test
    fun defaultIdIsNonNegativeRandom() {
        val builder = NotifierBuilder()
        assertTrue(builder.id >= 0)
    }

    @Test
    fun defaultsAreEmpty() {
        val builder = NotifierBuilder()
        assertEquals("", builder.title)
        assertEquals("", builder.body)
        assertEquals(emptyMap(), builder.payloadData)
        assertNull(builder.image)
        assertEquals(emptyList(), builder.actions)
        assertEquals(0L, builder.scheduledAt)
    }

    @Test
    fun payloadDslReplacesPayloadData() {
        val builder = NotifierBuilder()
        builder.payload {
            put("key1", "value1")
            put("key2", "value2")
        }
        assertEquals(mapOf("key1" to "value1", "key2" to "value2"), builder.payloadData)
    }

    @Test
    fun propertiesAreAssignable() {
        val builder = NotifierBuilder().apply {
            id = 7
            title = "title"
            body = "body"
            payloadData = mapOf("k" to "v")
            image = NotificationImage.Url("https://example.com/image.png")
            actions = listOf(NotificationAction(id = "a", title = "Action"))
            scheduledAt = 123L
        }
        assertEquals(7, builder.id)
        assertEquals("title", builder.title)
        assertEquals("body", builder.body)
        assertEquals(mapOf("k" to "v"), builder.payloadData)
        assertEquals(NotificationImage.Url("https://example.com/image.png"), builder.image)
        assertEquals(1, builder.actions.size)
        assertEquals(123L, builder.scheduledAt)
    }
}
