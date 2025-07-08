@file:Suppress("unused")

package flavor

import org.gradle.api.Plugin
import org.gradle.api.Project


class LibFlavorPlugin : Plugin<Project> {
    override fun apply(project: Project) = project.configureLibrary {
        applyFlavorConfig()
        applyFlavorSourceSet()
    }
}
