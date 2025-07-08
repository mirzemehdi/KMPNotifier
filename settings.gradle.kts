@file:Suppress("UnstableApiUsage")

rootProject.name = "KMPNotifierLib"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://developer.huawei.com/repo/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://developer.huawei.com/repo/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
include(":kmpnotifier")
include(":sample")
