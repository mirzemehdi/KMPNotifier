package com.mmk.kmpnotifier.internal

/**
 * Marks declarations that are internal to the KMPNotifier library modules.
 *
 * These declarations exist only for cross-module wiring between
 * `kmpnotifier-core`, `kmpnotifier-local`, `kmpnotifier-push-firebase`
 * and the `kmpnotifier` compatibility artifact.
 * They provide no compatibility guarantees and must not be used from application code.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Internal KMPNotifier API used for cross-module wiring. " +
            "It provides no compatibility guarantees and must not be used from application code.",
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS,
)
public annotation class InternalKMPNotifierApi
