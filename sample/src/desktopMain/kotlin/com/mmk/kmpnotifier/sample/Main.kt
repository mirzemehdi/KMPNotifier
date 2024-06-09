package com.mmk.kmpnotifier.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


fun main() = application {
    AppInitializer.onApplicationStart()
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPNotifier Desktop",
    ) {
        println("Desktop app is started")

        //Google Sign-In Button and authentication with Firebase
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Desktop app")
        }

    }
}