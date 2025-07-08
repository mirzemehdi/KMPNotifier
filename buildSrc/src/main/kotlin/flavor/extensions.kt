@file:Suppress("unused")

package flavor

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import flavor.Flavor.DIMENSION
import flavor.Flavor.flavors
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure


internal fun CommonExtension<*, *, *, *, *, *>.applyFlavorConfig() {
    defaultConfig { missingDimensionStrategy(dimension = DIMENSION, requestedValues = flavors) }

    flavorDimensions.add(DIMENSION)

    productFlavors {
        flavors.forEach { flavorName ->
            create(flavorName) { dimension = DIMENSION }
        }
    }
}

internal fun CommonExtension<*, *, *, *, *, *>.applyFlavorSourceSet() {
    sourceSets {
        flavors.forEach { flavorName ->
            named(flavorName) {
                val name = flavorName.replaceFirstChar { it.uppercase() }
                kotlin.srcDirs("src/android$name/kotlin")
                manifest.srcFile("src/android$name/AndroidManifest.xml")
            }
        }
    }
}

fun Project.configureLibrary(configure: LibraryExtension.() -> Unit) =
    plugins.withId("com.android.library") {
        extensions.configure<LibraryExtension>(configure)
    }

fun Project.configureApplication(configure: ApplicationExtension.() -> Unit) =
    plugins.withId("com.android.application") {
        extensions.configure<ApplicationExtension>(configure)
    }

fun DependencyHandlerScope.flavorImplementation(
    flavorName: String,
    dependency: Any
) = add(
    configurationName = "${flavorName}Implementation",
    dependencyNotation = dependency
)
