rootProject.name = "KMPNotifierLib"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
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
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":kmpnotifier-core")
include(":kmpnotifier-local")
include(":kmpnotifier-push-firebase")
include(":kmpnotifier")
project(":kmpnotifier").projectDir = file("deprecated/kmpnotifier")
include(":shared")
include(":androidApp")
include(":desktopApp")
include(":webApp")
