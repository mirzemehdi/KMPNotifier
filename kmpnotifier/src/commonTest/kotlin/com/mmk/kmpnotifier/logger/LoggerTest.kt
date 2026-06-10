package com.mmk.kmpnotifier.logger

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.testutil.TestDispatch
import com.mmk.kmpnotifier.testutil.TestNotifierState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LoggerTest {

    @BeforeTest
    fun setUp() = TestNotifierState.resetAll()

    @AfterTest
    fun tearDown() = TestNotifierState.resetAll()

    @Test
    fun defaultLoggerIsEmptyLogger() {
        assertSame(EmptyLogger, currentLogger)
    }

    @Test
    fun setLoggerReceivesInternalLogMessages() {
        val messages = mutableListOf<String>()
        NotifierManager.setLogger { messages.add(it) }

        TestDispatch.pushNotification(title = "t", body = "b")

        assertTrue(messages.isNotEmpty(), "Expected internal dispatch to produce log messages")
    }

    @Test
    fun loggerIsFunInterface() {
        val messages = mutableListOf<String>()
        val logger = Logger { messages.add(it) }
        logger.log("hello")
        assertTrue(messages.contains("hello"))
    }
}
