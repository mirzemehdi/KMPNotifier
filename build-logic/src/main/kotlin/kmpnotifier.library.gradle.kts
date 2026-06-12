import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * Convention plugin shared by all published KMPNotifier library modules.
 *
 * Configures: targets (android via the AGP 9 KMP library plugin, ios x3, jvm, js, wasmJs),
 * explicit API mode, shared test dependencies, and Maven Central publishing. The artifactId
 * is the Gradle project name; modules declare only their `kotlin.android.namespace`,
 * dependencies, POM name/description and module-specific blocks (e.g. swiftPMDependencies).
 */

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("com.vanniktech.maven.publish")
}

val libs = the<LibrariesForLibs>()

kotlin {
    explicitApi()

    android {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        // JVM-based unit tests (Robolectric) run on the host machine.
        withHostTest {
            isIncludeAndroidResources = true
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

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutine.test)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.junit)
            implementation(libs.robolectric)
            implementation(libs.androidx.test.core.ktx)
            implementation(libs.kotlinx.coroutine.android)
        }
    }
}

configure<MavenPublishBaseExtension> {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = true
        )
    )
    coordinates(
        "io.github.mirzemehdi",
        project.name,
        project.properties["kmpNotifierVersion"] as String
    )
    pom {
        url = "https://github.com/mirzemehdi/KMPNotifier/"
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        developers {
            developer {
                name.set("Mirzamehdi Karimov")
                email.set("mirzemehdi@gmail.com")
            }
        }
        scm {
            connection.set("https://github.com/mirzemehdi/KMPNotifier.git")
            url.set("https://github.com/mirzemehdi/KMPNotifier")
        }
        issueManagement {
            system.set("Github")
            url.set("https://github.com/mirzemehdi/KMPNotifier/issues")
        }
    }

    publishToMavenCentral()

    if (!project.gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }) {
        signAllPublications()
    }
}
