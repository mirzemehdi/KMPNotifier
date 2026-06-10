package com.mmk.kmpnotifier.extensions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PayloadConversionTest {

    @Test
    fun nullMapBecomesEmptyPayload() {
        val payload = (null as Map<Any?, *>?).asPayloadData()
        assertTrue(payload.isEmpty())
    }

    @Test
    fun nonStringKeysAreFiltered() {
        val payload = mapOf<Any?, Any?>(
            null to "nullKeyValue",
            42 to "intKeyValue",
            "stringKey" to "stringValue",
        ).asPayloadData()

        assertEquals(setOf("stringKey"), payload.keys)
        assertEquals("stringValue", payload["stringKey"])
    }

    @Test
    fun valuesArePreservedIncludingNulls() {
        val payload = mapOf<Any?, Any?>(
            "a" to 1,
            "b" to null,
            "c" to mapOf("nested" to true),
        ).asPayloadData()

        assertEquals(1, payload["a"])
        assertNull(payload["b"])
        assertTrue(payload.containsKey("b"))
        assertEquals(mapOf("nested" to true), payload["c"])
    }

    @Test
    fun firebaseMarkerKeyIsPreserved() {
        val payload = mapOf<Any?, Any?>(
            "gcm.message_id" to "msg-1",
            "data" to "value",
        ).asPayloadData()

        assertTrue(payload.containsKey("gcm.message_id"))
        assertEquals("msg-1", payload["gcm.message_id"])
    }
}
