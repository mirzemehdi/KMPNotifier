@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

plugins {
    id("kmpnotifier.library")
}

kotlin {
    android {
        namespace = "com.mmk.kmpnotifier.push.firebase"
    }

    swiftPMDependencies {
        iosMinimumDeploymentTarget = "16.0"
        // Firebase's transitive C/C++ modules (gRPC, abseil, ...) fail cinterop generation;
        // import only the FirebaseMessaging Clang module explicitly.
        discoverClangModulesImplicitly = false
        swiftPackage(
            url = url("https://github.com/firebase/firebase-ios-sdk.git"),
            // Range instead of exact: the bindings baked into the published klib are generated
            // against this minimum version, and firebase-ios-sdk keeps ObjC ABI compatibility
            // within a major. An exact pin makes this library the version bottleneck for every
            // consumer that needs a newer SDK feature (e.g. FID registration in 12.16.0).
            version = from("12.14.0"),
            products = listOf(product("FirebaseMessaging")),
            importedClangModules = listOf("FirebaseMessaging"),
        )
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.firebase.messaging)
        }
        commonMain.dependencies {
            api(projects.kmpnotifierLocal)
            implementation(libs.kotlinx.coroutine)
        }
    }
}

mavenPublishing {
    pom {
        name = "KMPNotifier Push Firebase"
        description = "Firebase push notifications module of KMPNotifier, Kotlin Multiplatform Notification Library"
    }
}
