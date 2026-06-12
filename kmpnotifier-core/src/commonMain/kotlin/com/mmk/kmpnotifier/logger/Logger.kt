package com.mmk.kmpnotifier.logger

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi

/**
 * Global logger interface for the KMPNotifier library.
 */
public fun interface Logger {
    /**
     * Log a message.
     *
     * @param message The message to log
     */
    public fun log(message: String)
}

/**
 * Default empty logger implementation that doesn't log anything.
 */
@InternalKMPNotifierApi
public object EmptyLogger : Logger {
    override fun log(message: String) {
        // Empty
    }
}

/**
 * The current logger used by the library.
 */
@InternalKMPNotifierApi
public var currentLogger: Logger = EmptyLogger
