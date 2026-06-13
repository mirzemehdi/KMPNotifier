package com.mmk.kmpnotifier.internal

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil

/**
 * Runtime services that the core module hands to a [com.mmk.kmpnotifier.KMPNotifierExtension]
 * during installation.
 */
@InternalKMPNotifierApi
public interface NotifierRuntime {
    public val configuration: NotificationPlatformConfiguration
    public val permissionUtil: PermissionUtil
}
