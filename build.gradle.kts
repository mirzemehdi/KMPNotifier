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

// Force-fix npm advisories in the Kotlin/JS dev toolchain lock (kotlin-js-store/):
// kotlinUpgradeYarnLock keeps lock entries that still satisfy their declared range,
// so every advisory fix is expressed as an explicit yarn resolution. brace-expansion
// is forced to 2.x for its 1.x consumers too (identical single-function API).
// Dev/build tooling only — nothing here ships in published artifacts.
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().apply {
        resolution("engine.io", "^6.6.7")
        resolution("ws", "^8.21.0")
        resolution("serialize-javascript", "^7.0.5")
        resolution("fast-uri", "^3.1.5")
        resolution("js-yaml", "^4.3.1")
        resolution("socket.io-parser", "^4.2.7")
        resolution("body-parser", "^1.20.6")
        resolution("tmp", "^0.2.6")
        resolution("follow-redirects", "^1.16.0")
        resolution("lodash", "^4.18.0")
        resolution("picomatch", "^2.3.2")
        resolution("flatted", "^3.4.2")
        resolution("brace-expansion", "^2.1.4")
    }
}
plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension>().apply {
        resolution("engine.io", "^6.6.7")
        resolution("ws", "^8.21.0")
        resolution("serialize-javascript", "^7.0.5")
    }
}
