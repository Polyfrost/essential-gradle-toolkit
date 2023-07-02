package cc.polyfrost.defaults

import cc.polyfrost.gradle.multiversion.Platform
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    `maven-publish`
}

val mavenUrl = findProperty("polyfrost.publishing.maven.url")?.toString() ?: throw GradleException("""
    No maven URL specified.
    You need to set `polyfrost.publishing.maven.url` in the project's `gradle.properties` file (e.g "https://repo.polyfrost.cc/releases/").
""".trimIndent())
val mavenName = findProperty("polyfrost.publishing.maven.name")?.toString() ?: throw GradleException("""
    No maven name specified.
    You need to set `polyfrost.publishing.maven.name` in the project's `gradle.properties` file (e.g "Polyfrost Maven").
""".trimIndent())
val mavenUsername = findProperty("polyfrost.publishing.maven.username")?.toString()
val mavenPassword = findProperty("polyfrost.publishing.maven.password")?.toString()

if (mavenUsername?.isNotBlank() == true && mavenPassword?.isNotBlank() == true) {
    extensions.configure<PublishingExtension>("publishing") {
        publications {
            register<MavenPublication>("maven") {
                from(components.getByName("java"))

                pluginManager.withPlugin("cc.polyfrost.multi-version") {
                    val platform: Platform by extensions
                    val baseArtifactId = (if (parent == rootProject) rootProject.name.lowercase() else null)
                        ?: project.findProperty("baseArtifactId")?.toString()
                        ?: throw GradleException("No default base maven artifact id found. Set `baseArtifactId` in the `gradle.properties` file of the multi-version-root project.")
                    artifactId = "$baseArtifactId-$platform"
                }
            }
        }

        repositories {
            maven(mavenUrl) {
                name = mavenName
                credentials {
                    this@credentials.username = mavenUsername
                    this@credentials.password = mavenPassword
                }
            }
        }
    }
}