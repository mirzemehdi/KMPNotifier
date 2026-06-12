@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.internal

import com.mmk.kmpnotifier.notification.PushNotifier
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class PushRegistryTest {

    @BeforeTest
    fun setUp() = NotifierInternals.resetForTests()

    @AfterTest
    fun tearDown() = NotifierInternals.resetForTests()

    @Test
    fun pushNotifierOrNullIsNullByDefault() {
        assertNull(NotifierInternals.pushNotifierOrNull())
    }

    @Test
    fun pushNotifierOrEmptyFallsBackToNoOpImplementation() = runTest {
        val empty = NotifierInternals.pushNotifierOrEmpty()
        assertNull(empty.getToken())
        empty.deleteMyToken()
        empty.subscribeToTopic("topic")
        empty.unSubscribeFromTopic("topic")
    }

    @Test
    fun registeredPushNotifierIsReturned() {
        val pushNotifier = object : PushNotifier {
            override suspend fun getToken(): String? = "token"
            override suspend fun deleteMyToken() = Unit
            override suspend fun subscribeToTopic(topic: String) = Unit
            override suspend fun unSubscribeFromTopic(topic: String) = Unit
        }

        NotifierInternals.registerPushNotifier(pushNotifier)

        assertSame(pushNotifier, NotifierInternals.pushNotifierOrNull())
        assertSame(pushNotifier, NotifierInternals.pushNotifierOrEmpty())
    }

    @Test
    fun resetClearsRegistry() {
        NotifierInternals.registerPushNotifier(object : PushNotifier {
            override suspend fun getToken(): String? = null
            override suspend fun deleteMyToken() = Unit
            override suspend fun subscribeToTopic(topic: String) = Unit
            override suspend fun unSubscribeFromTopic(topic: String) = Unit
        })

        NotifierInternals.resetForTests()

        assertNull(NotifierInternals.pushNotifierOrNull())
    }
}
