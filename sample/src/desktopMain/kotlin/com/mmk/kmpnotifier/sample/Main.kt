package com.mmk.kmpnotifier.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


fun main() = application {
    AppInitializer.onApplicationStart()
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPNotifier Desktop",
    ) {
        println("Desktop app is started")
        App()

    }
}