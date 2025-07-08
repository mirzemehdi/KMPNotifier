package com.mmk.kmpnotifier.logger

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

public fun Logger.log(throwable: Throwable): Unit = log(throwable.message.toString())

/**
 * Default empty logger implementation that doesn't log anything.
 */
internal object EmptyLogger : Logger {
    override fun log(message: String) = Unit
}

/**
 * The current logger used by the library.
 */
internal var currentLogger: Logger = EmptyLogger
