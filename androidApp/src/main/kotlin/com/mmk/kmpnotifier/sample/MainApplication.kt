package com.mmk.kmpnotifier.sample

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.onApplicationStart()
    }
}