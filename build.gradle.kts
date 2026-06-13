plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidKmpLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlinx.binary.validator)
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.google.services) apply false
}

dependencies {
    dokka(projects.kmpnotifierCore)
    dokka(projects.kmpnotifierLocal)
    dokka(projects.kmpnotifierPushFirebase)
    dokka(projects.kmpnotifier)
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
    ignoredProjects += listOf("shared", "androidApp", "desktopApp", "webApp")
}

allprojects {
    group = "io.github.mirzemehdi"
    version = project.properties["kmpNotifierVersion"] as String


    val excludedModules = listOf(":shared", ":androidApp", ":desktopApp", ":webApp")
    if (project.path in excludedModules) return@allprojects

    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
}





