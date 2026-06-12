@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class KMPNotifierNotInitializedTest {

    @BeforeTest
    fun setUp() = NotifierInternals.resetForTests()

    @AfterTest
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun isInitializedIsFalseBeforeInitialization() {
        assertFalse(KMPNotifier.isInitialized)
    }

    @Test
    fun permissionUtilThrowsBeforeInitialization() {
        assertFailsWith<IllegalStateException> { KMPNotifier.permissionUtil }
    }

    @Test
    fun configurationThrowsBeforeInitialization() {
        assertFailsWith<IllegalStateException> { NotifierInternals.configuration }
    }

    @Test
    fun listenerRegistrationWorksBeforeInitialization() {
        KMPNotifier.addListener(object : KMPNotifier.Listener {})
        KMPNotifier.setListener(null)
    }
}
