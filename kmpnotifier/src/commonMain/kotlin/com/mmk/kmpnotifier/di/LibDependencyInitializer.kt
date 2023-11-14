package com.mmk.kmpnotifier.di


import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module


internal object LibDependencyInitializer {
    var koinApp: KoinApplication? = null
        private set


    fun initialize(configuration: NotificationPlatformConfiguration) {
        if (isInitialized()) return
        val configModule = module {
            single { configuration }
        }
        koinApp = koinApplication {
            modules(configModule + platformModule)
        }.also {
            it.koin.onLibraryInitialized()
        }

    }

    fun isInitialized() = koinApp != null


}

private fun Koin.onLibraryInitialized() {
    println("Library is initialized")
    val permissionUtil by inject<PermissionUtil>()
    val pushNotifier by inject<PushNotifier>()

    //In Android platform permission should be asked in activity
    if (isAndroidPlatform().not())
        permissionUtil.askNotificationPermission()

    pushNotifier.doAfterInitialization()


}

