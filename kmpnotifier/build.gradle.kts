import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinNativeCocoaPods)

}

kotlin {
    explicitApi()
    androidTarget {
        publishAllLibraryVariants()
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//    }
//    js(IR) {
//        nodejs()
//        browser()
//        binaries.library()
//    }

//    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()


//    cocoapods {
//        ios.deploymentTarget = "14.1"
//        framework {
//            baseName = "KMPNotifier"
//            isStatic = true
//        }
//        noPodspec()
//        pod("FirebaseMessaging")
//    }



    sourceSets {

        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.ktx)
            implementation(libs.firebase.messaging)

        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutine)
            implementation(libs.prinum.logger)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
            val mainCompilation = compilations.getByName("main")

            // point our crashlytics.def file
            mainCompilation.cinterops.create("crashlytics") {
                // Pass the header files location
                includeDirs("$projectDir/src/include")
                compilerOpts("-DNS_FORMAT_ARGUMENT(A)=", "-D_Nullable_result=_Nullable")
            }
        }
    }
}

android {
    namespace = "com.mmk.kmpnotifier"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

