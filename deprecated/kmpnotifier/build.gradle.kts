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

// The deprecated umbrella is published only when explicitly requested
// (-PpublishDeprecatedUmbrella=true). 2.0.0 ships it one last time for the
// 1.x drop-in upgrade path; later releases skip it.
tasks.withType<org.gradle.api.publish.maven.tasks.AbstractPublishToMaven>().configureEach {
    enabled = providers.gradleProperty("publishDeprecatedUmbrella").orNull == "true"
}
