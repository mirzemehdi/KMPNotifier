package com.mmk.kmpnotifier.extensions

import java.io.File

internal sealed interface DesktopPlatform {
    data object Linux : DesktopPlatform
    data object Windows : DesktopPlatform
    data object MacOs : DesktopPlatform
}

internal fun getDesktopPlatformType(): DesktopPlatform? {
    val name = System.getProperty("os.name")
    return when {
        name?.contains("Linux") == true -> DesktopPlatform.Linux
        name?.contains("Win") == true -> DesktopPlatform.Windows
        name?.contains("Mac") == true -> DesktopPlatform.MacOs
        else -> null
    }
}

public fun composeDesktopResourcesPath(): String? {
    return runCatching {
        val resourcesDirectory = File(System.getProperty("compose.application.resources.dir"))
        return resourcesDirectory.canonicalPath
    }.getOrNull()

}