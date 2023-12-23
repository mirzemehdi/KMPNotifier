package com.mmk.kmpnotifier.sample

import android.app.Application
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.onApplicationStart()
    }
}