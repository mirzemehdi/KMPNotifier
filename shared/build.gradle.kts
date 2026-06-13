import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    android {
        namespace = "com.mmk.kmpnotifier.sample.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        // Notification icon drawables and the custom sound live in this module's res/.
        androidResources { enable = true }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    jvm("desktop")
    // No iosX64: Compose Multiplatform 1.11+ has no Intel-simulator artifacts.
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            export(project(":kmpnotifier-core"))
            export(project(":kmpnotifier-local"))
            export(project(":kmpnotifier-push-firebase"))
            baseName = "shared"
            isStatic = true
        }
    }
    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.components.resources)
            api(project(":kmpnotifier-core"))
            api(project(":kmpnotifier-local"))
            api(project(":kmpnotifier-push-firebase"))
        }
        desktopMain.dependencies {
            implementation(compose.desktop.common)
        }
    }
}
