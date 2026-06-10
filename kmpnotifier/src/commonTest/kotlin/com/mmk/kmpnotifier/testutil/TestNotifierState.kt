package com.mmk.kmpnotifier.testutil

import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.logger.EmptyLogger
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.NotifierManagerImpl

/**
 * Restores the library to a clean state between tests.
 */
internal object TestNotifierState {

    fun resetAll() {
        LibDependencyInitializer.reset()
        NotifierManagerImpl.reset()
        currentLogger = EmptyLogger
    }
}
