@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.testutil

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.notification.NotifierManagerImpl

/**
 * Restores the library to a clean state between tests.
 */
internal object TestNotifierState {

    fun resetAll() {
        NotifierInternals.resetForTests()
        NotifierManagerImpl.reset()
    }
}
