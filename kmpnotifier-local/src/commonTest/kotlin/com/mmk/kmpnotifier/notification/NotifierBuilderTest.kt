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
    fun payloadDslReplacesPayloadData() {
        val builder = NotifierBuilder()
        builder.payload {
            put("key1", "value1")
            put("key2", "value2")
        }
        assertEquals(mapOf("key1" to "value1", "key2" to "value2"), builder.payloadData)
    }

}
