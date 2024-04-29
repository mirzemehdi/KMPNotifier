package com.mmk.kmpnotifier.di

import android.content.Context
import androidx.startup.Initializer
import com.mmk.kmpnotifier.firebase.FirebasePushNotifierImpl
import com.mmk.kmpnotifier.notification.AndroidNotifier
import com.mmk.kmpnotifier.notification.NotificationChannelFactory
import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.PushNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.AndroidMockPermissionUtil
import com.mmk.kmpnotifier.permission.PermissionUtil
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

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


internal actual val platformModule = module {
    factory { Platform.Android } bind Platform::class
    single { applicationContext }
    factoryOf(::AndroidMockPermissionUtil) bind PermissionUtil::class
    factory {
        val configuration =
            get<NotificationPlatformConfiguration>() as NotificationPlatformConfiguration.Android
        AndroidNotifier(
            context = get(),
            androidNotificationConfiguration = configuration,
            notificationChannelFactory = NotificationChannelFactory(
                context = get(),
                channelData = configuration.notificationChannelData
            ),
            permissionUtil = get()
        )
    } bind Notifier::class

    factoryOf(::FirebasePushNotifierImpl) bind PushNotifier::class

}


