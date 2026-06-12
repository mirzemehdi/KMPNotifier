package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.permission.PermissionUtil
import com.mmk.kmpnotifier.permission.WebPermissionUtilImpl

internal actual val platform: Platform = Platform.Web

internal actual fun createPermissionUtil(): PermissionUtil = WebPermissionUtilImpl()
