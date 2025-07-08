@file:Suppress("unused")

package flavor

import org.gradle.api.Plugin
import org.gradle.api.Project


class AppFlavorPlugin : Plugin<Project> {
    override fun apply(project: Project) = project.configureApplication { applyFlavorConfig() }
}
