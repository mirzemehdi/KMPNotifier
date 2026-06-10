package com.mmk.kmpnotifier.extensions

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.mmk.kmpnotifier.internal.InternalKMPNotifierApi

@InternalKMPNotifierApi
public val Context.notificationManager: NotificationManager?
    get() = this.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

@InternalKMPNotifierApi
public fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

@InternalKMPNotifierApi
public fun Context.hasNotificationPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        hasPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else true
