package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.permission.EmptyPermissionUtilImpl
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    factory { Platform.Desktop } bind Platform::class
    factoryOf(::EmptyPermissionUtilImpl) bind PermissionUtil::class
}
