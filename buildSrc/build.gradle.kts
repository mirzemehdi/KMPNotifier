@file:Suppress("UnstableApiUsage")



plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(libs.gradle.android)
}

group = "com.mmk.kmpnotifier"
gradlePlugin {
    plugins {
        val name = "flavor.AppFlavorPlugin"
        register(name) {
            id = "com.mmk.kmpnotifier"
            implementationClass = name
        }
    }
}

gradlePlugin {
    plugins {
        val name = "flavor.LibFlavorPlugin"
        register(name) {
            id = "com.mmk.kmpnotifier.library"
            implementationClass = name
        }
    }
}
