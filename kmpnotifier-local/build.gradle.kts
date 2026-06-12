plugins {
    id("kmpnotifier.library")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
        commonMain.dependencies {
            api(projects.kmpnotifierCore)
            implementation(libs.kotlinx.coroutine)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}

android {
    namespace = "com.mmk.kmpnotifier.local"
}

mavenPublishing {
    pom {
        name = "KMPNotifier Local"
        description = "Local notifications module of KMPNotifier, Kotlin Multiplatform Notification Library"
    }
}
