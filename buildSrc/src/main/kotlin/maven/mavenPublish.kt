@file:Suppress("unused")

package maven

import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomLicenseSpec


fun MavenPom.configure(
    libraryName: String,
    libraryDescription: String,
    developerName: String,
    developerEmail: String,
    githubUsername: String,
    githubRepositoryName: String,
    licenseConfig: MavenPomLicenseSpec.() -> Unit = {
        name.set("Apache-2.0")
        url.set("https://opensource.org/licenses/Apache-2.0")
    }
) {
    val repoUrl = "https://github.com/$githubUsername/$githubRepositoryName"
    url.set(repoUrl)
    name.set(libraryName)
    description.set(libraryDescription)
    licenses {
        license { licenseConfig() }
    }
    developers {
        developer {
            name.set(developerName)
            email.set(developerEmail)
        }
    }
    scm {
        url.set(repoUrl)
        connection.set("$repoUrl.git")
    }
    issueManagement {
        system.set("Github")
        url.set("$repoUrl/issues")
    }
}
