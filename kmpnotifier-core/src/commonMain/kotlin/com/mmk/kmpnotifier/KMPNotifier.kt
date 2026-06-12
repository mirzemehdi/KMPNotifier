@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier

import com.mmk.kmpnotifier.di.LibDependencyInitializer
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.internal.NotifierEventHub
import com.mmk.kmpnotifier.internal.NotifierInternals
import com.mmk.kmpnotifier.logger.Logger
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

/**
 * Main entry point of the KMPNotifier library.
 *
 * Initialize it once on application start, passing the platform configuration and the
 * capabilities (extensions) you want to use:
 *
 * ```
 * // Local notifications only:
 * KMPNotifier.initialize(configuration, LocalNotifications)
 *
 * // Local + Firebase push (android/ios, requires kmpnotifier-push-firebase):
 * KMPNotifier.initialize(configuration, FirebasePush)
 * ```
 *
 * Local notifications are then available through `KMPNotifier.localNotifier`
 * (or `LocalNotifications.notifier`), and push through `KMPNotifier.firebasePushNotifier`
 * (or `FirebasePush.notifier`).
 */
public object KMPNotifier {

    /**
     * Initializes the library. Call on application start.
     * On iOS call it from the main thread (e.g. in `didFinishLaunchingWithOptions`) —
     * the notification-center delegate is installed during initialization.
     * Calling it again is a no-op for the configuration; extensions not yet installed
     * are still installed.
     *
     * @param configuration platform configuration, e.g. [NotificationPlatformConfiguration.Android]
     * @param extensions capabilities to install, e.g. `LocalNotifications`, `FirebasePush`
     */
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC // Swift/ObjC callers use the List overload below (NSArray bridges naturally)
    public fun initialize(
        configuration: NotificationPlatformConfiguration,
        vararg extensions: KMPNotifierExtension,
    ) {
        initialize(configuration, extensions.toList())
    }

    /**
     * Initializes the library. Call on application start.
     *
     * Swift example:
     * ```swift
     * KMPNotifier.shared.initialize(
     *     configuration: NotificationPlatformConfigurationIos(...),
     *     extensions: [FirebasePush.shared]
     * )
     * ```
     *
     * @param configuration platform configuration, e.g. [NotificationPlatformConfiguration.Ios]
     * @param extensions capabilities to install, e.g. `LocalNotifications`, `FirebasePush`
     */
    public fun initialize(
        configuration: NotificationPlatformConfiguration,
        extensions: List<KMPNotifierExtension>,
    ) {
        LibDependencyInitializer.initialize(configuration)
        NotifierInternals.installExtensions(extensions)
    }

    /** Whether [initialize] has been called. */
    public val isInitialized: Boolean
        get() = LibDependencyInitializer.isInitialized()

    /**
     * Permission util to check and ask notification permission.
     * On Android, runtime permission must be requested in an Activity —
     * see `ComponentActivity.permissionUtil()`.
     *
     * @throws IllegalStateException if the library is not initialized
     */
    public val permissionUtil: PermissionUtil
        get() = NotifierInternals.permissionUtil

    /**
     * Sets a logger for the library's internal logs.
     * By default nothing is logged.
     */
    public fun setLogger(logger: Logger) {
        currentLogger = logger
    }

    /** Adds a listener for shared notification events. */
    public fun addListener(listener: Listener) {
        NotifierEventHub.addListener(listener)
    }

    /** Removes a previously added listener. */
    public fun removeListener(listener: Listener) {
        NotifierEventHub.removeListener(listener)
    }

    /** Replaces all listeners with the given one. Pass null to remove all listeners. */
    public fun setListener(listener: Listener?) {
        NotifierEventHub.setListener(listener)
    }

    /**
     * Shared notification events, fired for both local and push notifications.
     *
     * Push-specific events (token changes, push payloads) are part of the push module —
     * see `PushListener`, registered via `KMPNotifier.addPushListener` (in `kmpnotifier-push-firebase`).
     */
    public interface Listener {

        /**
         * Called when a notification is clicked.
         * @param data notification payload data
         */
        public fun onNotificationClicked(data: PayloadData) {}

        /**
         * Called when a notification action button is triggered.
         * @param actionId the ID of the triggered action
         * @param notificationId the ID of the notification
         * @param payload the payload data of the notification
         */
        public fun onAction(actionId: String, notificationId: Int, payload: PayloadData) {}
    }
}
