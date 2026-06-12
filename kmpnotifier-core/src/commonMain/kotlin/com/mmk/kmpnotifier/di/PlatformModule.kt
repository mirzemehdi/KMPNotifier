package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.permission.PermissionUtil


internal sealed interface Platform {
    data object Android : Platform
    data object Ios : Platform
    data object Desktop : Platform
    data object Web : Platform
}

/** The platform this source set is compiled for. */
internal expect val platform: Platform

/** Creates the platform's permission util. Called once during initialization. */
internal expect fun createPermissionUtil(): PermissionUtil
