package com.mmk.kmpnotifier.di

import org.koin.core.module.Module


internal sealed interface Platform {
    data object Android : Platform
    data object Ios : Platform
    data object Desktop : Platform
    data object Web : Platform
}
internal expect val platformModule: Module