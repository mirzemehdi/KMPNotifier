package com.mmk.kmpnotifier.notification

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNull

class EmptyPushNotifierTest {

    @Test
    fun getTokenReturnsNull() = runTest {
        assertNull(EmptyPushNotifierImpl().getToken())
    }

    @Test
    fun deleteMyTokenDoesNotThrow() = runTest {
        EmptyPushNotifierImpl().deleteMyToken()
    }

    @Test
    fun subscribeToTopicDoesNotThrow() = runTest {
        EmptyPushNotifierImpl().subscribeToTopic("topic")
    }

    @Test
    fun unSubscribeFromTopicDoesNotThrow() = runTest {
        EmptyPushNotifierImpl().unSubscribeFromTopic("topic")
    }
}
