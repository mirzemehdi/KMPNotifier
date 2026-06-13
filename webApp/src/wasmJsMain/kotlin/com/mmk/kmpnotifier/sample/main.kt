package com.mmk.kmpnotifier.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onApplicationStartPlatformSpecific()
    ComposeViewport(document.body!!) { App() }
}
