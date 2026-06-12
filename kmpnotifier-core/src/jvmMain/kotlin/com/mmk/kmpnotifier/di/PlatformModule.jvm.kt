package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.permission.EmptyPermissionUtilImpl
import com.mmk.kmpnotifier.permission.PermissionUtil

internal actual val platform: Platform = Platform.Desktop

internal actual fun createPermissionUtil(): PermissionUtil = EmptyPermissionUtilImpl()
