buildscript {
    dependencies {
        classpath(libs.huawei.services)
    }
}

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinNativeCocoaPods) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlinx.binary.validator)
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.google.services) apply false
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
    ignoredProjects += "sample"
}

allprojects {
    val excludedModules = listOf(":sample")
    if (project.path in excludedModules) return@allprojects

    apply(plugin = "maven-publish")
    apply(plugin = "signing")
}
