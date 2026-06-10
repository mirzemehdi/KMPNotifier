package com.mmk.kmpnotifier.di

import com.mmk.kmpnotifier.permission.PermissionUtil
import com.mmk.kmpnotifier.permission.WebPermissionUtilImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal actual val platformModule = module {
    factory { Platform.Web } bind Platform::class
    factoryOf(::WebPermissionUtilImpl) bind PermissionUtil::class
}
