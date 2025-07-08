import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import flavor.flavorImplementation
import flavor.Flavor
import maven.configure

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinNativeCocoaPods)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.kmpnotifier.library)
    alias(libs.plugins.google.services)
    alias(libs.plugins.dokka)
}

kotlin {
    explicitApi()

    androidTarget {
        publishAllLibraryVariants()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    js(IR) {
        nodejs()
        browser()
        binaries.library()
    }

    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        ios.deploymentTarget = "14.1"
        framework {
            baseName = Config.LIBRARY_NAME
            isStatic = true
        }
        noPodspec()
        pod("FirebaseMessaging")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.bundles.androidMain)
        }

        commonMain.dependencies {
            implementation(libs.bundles.commonMain)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = Config.PACKAGE_ID
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        flavorImplementation(flavorName = Flavor.HUAWEI, dependency = libs.huawei.push)
        flavorImplementation(flavorName = Flavor.GOOGLE, dependency = libs.firebase.messaging)
    }
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )
    coordinates(
        groupId = Config.ARTIFACT_GROUP_ID,
        artifactId = Config.ARTIFACT_ID,
        version = Config.ARTIFACT_VERSION
    )
    pom {
        configure(
            libraryName = Config.LIBRARY_NAME,
            libraryDescription = Config.LIBRARY_DESCRIPTION,
            developerName = Config.DEVELOPER_NAME,
            developerEmail = Config.DEVELOPER_EMAIL,
            githubUsername = Config.GITHUB_USERNAME,
            githubRepositoryName = Config.GITHUB_REPOSITORY_NAME,
        )
    }

    publishToMavenCentral()
    signAllPublications()
}
