package com.mmk.kmpnotifier.notification

import kotlin.test.Test
import kotlin.test.assertEquals

class IosNotificationImagesTest {

    private fun bytes(vararg values: Int) = ByteArray(values.size) { values[it].toByte() }

    @Test
    fun detectsPngFromMagicNumber() {
        assertEquals("png", imageFileExtension(bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A), hint = "jpg"))
    }

    @Test
    fun detectsJpegFromMagicNumber() {
        assertEquals("jpg", imageFileExtension(bytes(0xFF, 0xD8, 0xFF, 0xE0), hint = "png"))
    }

    @Test
    fun detectsGifFromMagicNumber() {
        assertEquals("gif", imageFileExtension(bytes(0x47, 0x49, 0x46, 0x38, 0x39, 0x61), hint = null))
    }

    @Test
    fun unknownHeaderFallsBackToSupportedHint() {
        assertEquals("png", imageFileExtension(bytes(0x00, 0x01), hint = "PNG"))
    }

    @Test
    fun unknownHeaderAndUnsupportedHintFallsBackToJpg() {
        assertEquals("jpg", imageFileExtension(ByteArray(0), hint = "webp"))
        assertEquals("jpg", imageFileExtension(ByteArray(0), hint = null))
    }
}
