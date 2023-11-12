package com.mmk.kmpnotifier.di

import org.koin.core.module.Module

internal expect fun isAndroidPlatform(): Boolean
internal expect val platformModule: Module