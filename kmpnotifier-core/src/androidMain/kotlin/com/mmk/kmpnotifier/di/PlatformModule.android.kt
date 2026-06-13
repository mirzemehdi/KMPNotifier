package com.mmk.kmpnotifier.di

import android.content.Context
import androidx.startup.Initializer
import com.mmk.kmpnotifier.permission.AndroidMockPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil

internal lateinit var applicationContext: Context
    private set

internal class ContextInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        applicationContext = context.applicationContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}


internal actual val platform: Platform = Platform.Android

internal actual fun createPermissionUtil(): PermissionUtil {
    check(::applicationContext.isInitialized) {
        "Application context is not available. androidx-startup is disabled or did not run — " +
                "initialize with KMPNotifier.initialize(context, configuration, ...extensions) instead."
    }
    return AndroidMockPermissionUtil(applicationContext)
}
