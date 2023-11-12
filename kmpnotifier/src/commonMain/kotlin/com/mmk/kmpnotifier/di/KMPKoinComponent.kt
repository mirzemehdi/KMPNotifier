package com.mmk.kmpnotifier.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

public abstract class KMPKoinComponent : KoinComponent {
    override fun getKoin(): Koin = LibDependencyInitializer.koinApp?.koin!!
}