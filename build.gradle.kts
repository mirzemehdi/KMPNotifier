import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.dokka.gradle.DokkaTask

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

    id("com.google.gms.google-services") version "4.4.0" apply false
}




allprojects {
    group = "io.github.mirzemehdi"
    version = "1.2.0-alpha"
    val sonatypeUsername = gradleLocalProperties(rootDir).getProperty("sonatypeUsername")
    val sonatypePassword = gradleLocalProperties(rootDir).getProperty("sonatypePassword")
    val gpgKeySecret = gradleLocalProperties(rootDir).getProperty("gpgKeySecret")
    val gpgKeyPassword = gradleLocalProperties(rootDir).getProperty("gpgKeyPassword")

    val excludedModules = listOf(":sample")
    if (project.path in excludedModules) return@allprojects

    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")


    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                val isSnapshot = version.toString().endsWith("SNAPSHOT")
                val repositoryId = System.getenv("SONATYPE_REPOSITORY_ID") ?: ""
                url = uri(
                    when{
                        isSnapshot.not() && repositoryId.isNotEmpty() -> "https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/${repositoryId}/"
                        isSnapshot.not() -> "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
                        else -> "https://s01.oss.sonatype.org/content/repositories/snapshots"
                    }
                )
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }

        val javadocJar = tasks.register<Jar>("javadocJar") {
            dependsOn(tasks.getByName<DokkaTask>("dokkaHtml"))
            archiveClassifier.set("javadoc")
            from("${layout.buildDirectory}/dokka")
        }

        publications {
            withType<MavenPublication> {
                artifact(javadocJar)
                pom {
                    groupId="io.github.mirzemehdi"
                    name.set("KMPNotifier")
                    description.set(" Kotlin Multiplatform Push Notification Library targeting ios and android")
                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://opensource.org/licenses/Apache-2.0")
                        }
                    }
                    url.set("mirzemehdi.github.io/KMPNotifier/")
                    issueManagement {
                        system.set("Github")
                        url.set("https://github.com/mirzemehdi/KMPNotifier/issues")
                    }
                    scm {
                        connection.set("https://github.com/mirzemehdi/KMPNotifier.git")
                        url.set("https://github.com/mirzemehdi/KMPNotifier")
                    }
                    developers {
                        developer {
                            name.set("Mirzamehdi Karimov")
                            email.set("mirzemehdi@gmail.com")
                        }
                    }
                }
            }
        }
    }

    val publishing = extensions.getByType<PublishingExtension>()
    extensions.configure<SigningExtension> {
        useInMemoryPgpKeys(gpgKeySecret, gpgKeyPassword)
        sign(publishing.publications)
    }

    // TODO: remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
    project.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
        dependsOn(project.tasks.withType(Sign::class.java))
    }
}


