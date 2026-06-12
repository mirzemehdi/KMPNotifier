plugins {
    id("kmpnotifier.library")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.ktx)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutine)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}

android {
    namespace = "com.mmk.kmpnotifier.core"
}

mavenPublishing {
    pom {
        name = "KMPNotifier Core"
        description = "Core module of KMPNotifier, Kotlin Multiplatform Notification Library"
    }
}
