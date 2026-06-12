package com.mmk.kmpnotifier.notification

import com.mmk.kmpnotifier.testutil.TestNotifierState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NotInitializedTest {

    @BeforeTest
    fun setUp() = TestNotifierState.resetAll()

    @AfterTest
    fun tearDown() = TestNotifierState.resetAll()

    @Test
    fun getLocalNotifierThrowsBeforeInitialization() {
        assertFailsWith<IllegalStateException> { NotifierManager.getLocalNotifier() }
    }

    @Test
    fun getPushNotifierThrowsBeforeInitialization() {
        assertFailsWith<IllegalStateException> { NotifierManager.getPushNotifier() }
    }

    @Test
    fun getPermissionUtilThrowsBeforeInitialization() {
        assertFailsWith<IllegalStateException> { NotifierManager.getPermissionUtil() }
    }

    @Test
    fun addListenerWorksBeforeInitialization() {
        // Listener registration is allowed pre-initialization (e.g. registered at app start).
        NotifierManager.addListener(object : NotifierManager.Listener {})
        NotifierManager.setListener(null)
    }
}
