package com.mmk.kmpnotifier.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onApplicationStartPlatformSpecific()
    CanvasBasedWindow(canvasElementId = "ComposeTarget") { App() }
}