plugins {
    id("kmpnotifier.library")
}

kotlin {
    android {
        namespace = "com.mmk.kmpnotifier"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.kmpnotifierCore)
            api(projects.kmpnotifierLocal)
            api(projects.kmpnotifierPushFirebase)
        }
    }
}

mavenPublishing {
    pom {
        name = "KMPNotifier"
        description = "Deprecated compatibility umbrella of KMPNotifier; depends on kmpnotifier-core, kmpnotifier-local and kmpnotifier-push-firebase"
    }
}
