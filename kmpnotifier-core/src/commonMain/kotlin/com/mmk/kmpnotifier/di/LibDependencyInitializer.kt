@file:OptIn(InternalKMPNotifierApi::class)

package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi
import com.mmk.kmpnotifier.logger.currentLogger
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil

/**
 * Dependencies created once at [LibDependencyInitializer.initialize].
 * Wired manually — the library deliberately has no dependency-injection framework
 * on its runtime classpath.
 */
internal class NotifierDependencies(
    val configuration: NotificationPlatformConfiguration,
    val permissionUtil: PermissionUtil,
)

internal object LibDependencyInitializer {

    var dependencies: NotifierDependencies? = null
        private set

    fun initialize(configuration: NotificationPlatformConfiguration) {
        if (isInitialized()) return
        dependencies = NotifierDependencies(
            configuration = configuration,
            permissionUtil = createPermissionUtil(),
        ).also { onLibraryInitialized(it) }
    }

    fun isInitialized() = dependencies != null

    /**
     * Clears initialization state.
     * Intended for tests to restore a clean library state between runs.
     */
    fun reset() {
        dependencies = null
    }

}

private fun onLibraryInitialized(dependencies: NotifierDependencies) {
    currentLogger.log("Library is initialized")
    val configuration = dependencies.configuration

    when (platform) {
        Platform.Android, Platform.Desktop -> Unit //In Android platform permission should be asked in activity
        Platform.Ios -> {
            val askNotificationPermissionOnStart =
                (configuration as? NotificationPlatformConfiguration.Ios)?.askNotificationPermissionOnStart
                    ?: true
            if (askNotificationPermissionOnStart) dependencies.permissionUtil.askNotificationPermission()
        }

        Platform.Web -> {
            val askNotificationPermissionOnStart =
                (configuration as? NotificationPlatformConfiguration.Web)?.askNotificationPermissionOnStart
                    ?: true
            if (askNotificationPermissionOnStart) dependencies.permissionUtil.askNotificationPermission()
        }

    }
}
