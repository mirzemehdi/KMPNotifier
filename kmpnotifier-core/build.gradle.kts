plugins {
    id("kmpnotifier.library")
}

kotlin {
    android {
        namespace = "com.mmk.kmpnotifier.core"
    }

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

mavenPublishing {
    pom {
        name = "KMPNotifier Core"
        description = "Core module of KMPNotifier, Kotlin Multiplatform Notification Library"
    }
}
