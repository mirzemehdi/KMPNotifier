package com.mmk.kmpnotifier.extensions

import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DesktopPlatformExtTest {

    private val resourcesDirProperty = "compose.application.resources.dir"
    private var originalValue: String? = null

    @BeforeTest
    fun setUp() {
        originalValue = System.getProperty(resourcesDirProperty)
        System.clearProperty(resourcesDirProperty)
    }

    @AfterTest
    fun tearDown() {
        if (originalValue != null) System.setProperty(resourcesDirProperty, originalValue!!)
        else System.clearProperty(resourcesDirProperty)
    }

    @Test
    fun composeDesktopResourcesPathReturnsNullWithoutProperty() {
        assertNull(composeDesktopResourcesPath())
    }

    @Test
    fun composeDesktopResourcesPathReturnsCanonicalPathWhenPropertySet() {
        val tmpDir = System.getProperty("java.io.tmpdir")
        System.setProperty(resourcesDirProperty, tmpDir)
        assertEquals(File(tmpDir).canonicalPath, composeDesktopResourcesPath())
    }

    @Test
    fun desktopPlatformTypeMatchesOsName() {
        val osName = System.getProperty("os.name").orEmpty()
        val expected = when {
            osName.contains("Linux") -> DesktopPlatform.Linux
            osName.contains("Win") -> DesktopPlatform.Windows
            osName.contains("Mac") -> DesktopPlatform.MacOs
            else -> null
        }
        assertEquals(expected, getDesktopPlatformType())
    }
}
