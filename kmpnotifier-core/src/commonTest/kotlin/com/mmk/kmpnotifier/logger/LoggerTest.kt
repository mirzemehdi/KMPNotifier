@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.logger

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class LoggerTest {

    @BeforeTest
    fun setUp() = NotifierInternals.resetForTests()

    @AfterTest
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun setLoggerReceivesInternalLogMessages() {
        val messages = mutableListOf<String>()
        KMPNotifier.setLogger { messages.add(it) }

        NotifierEventHub.emitPushNotification(title = "t", body = "b")

        assertTrue(messages.isNotEmpty(), "Expected internal dispatch to produce log messages")
    }

}
