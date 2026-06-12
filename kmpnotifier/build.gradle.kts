plugins {
    id("kmpnotifier.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.kmpnotifierCore)
            api(projects.kmpnotifierLocal)
            api(projects.kmpnotifierPushFirebase)
        }
    }
}

android {
    namespace = "com.mmk.kmpnotifier"
}

mavenPublishing {
    pom {
        name = "KMPNotifier"
        description = "Deprecated compatibility umbrella of KMPNotifier; depends on kmpnotifier-core, kmpnotifier-local and kmpnotifier-push-firebase"
    }
}
