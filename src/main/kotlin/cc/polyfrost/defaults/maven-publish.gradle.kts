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
                    val baseArtifactId = (if (parent == rootProject) rootProject.name.toLowerCase() else null)
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

    pluginManager.withPlugin("cc.polyfrost.multi-version") {
        val platform: Platform by extensions

        if (platform.isLegacyForge) {
            // For legacy Forge we publish the dev jar rather than the srg mapped one, so the consumers do not need to deobf
            // (does not sound like best practise but that is how most mods do it and there isn't really any diversity in
            // mappings anyway).
            // To do that, we first stop loom from adding the remapped artifact,
            (extensions.getByName("loom") as LoomGradleExtensionAPI).setupRemappedVariants.set(false)
            // then remove the default artifact (which has a -dev classifier) from all configurations and finally re-add
            // it without the dev classifier.
            afterEvaluate {
                configurations.all {
                    if (artifacts.removeIf { it.classifier == "dev" }) {
                        project.artifacts.add(name, tasks.named("jar")) {
                            classifier = null
                        }
                    }
                    // And the same for the sources jar
                    if (artifacts.removeIf { it.classifier == "sources-dev" }) {
                        project.artifacts.add(name, tasks.named("sourcesJar")) {
                            classifier = "sources"
                        }
                    }
                }
            }
        }

        // Dependencies added to modApi get automatically added to apiElements by Loom, but it does not add them to
        // runtimeElements, which causes issues when another project depends on this one via one of the mod* configurations
        // because those seem to be reading the runtimeElements.
        // To work around that, we'll just disable the Gradle Module Metadata and just use maven pom only.
        tasks.withType<GenerateModuleMetadata> {
            enabled = false
        }
    }
}