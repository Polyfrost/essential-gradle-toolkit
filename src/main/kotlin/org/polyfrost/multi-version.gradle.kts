package org.polyfrost

import org.polyfrost.gradle.multiversion.Platform
import org.polyfrost.gradle.util.setupLoomPlugin
import com.replaymod.gradle.preprocess.PreprocessExtension
import com.replaymod.gradle.preprocess.PreprocessPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
}

val platform = Platform.of(project)

extensions.add("platform", platform)

setupLoomPlugin(platform) {
    runConfigs.all {
        isIdeConfigGenerated = true
    }
}
setupPreprocessPlugin()
configureJavaVersion()
afterEvaluate { configureResources() } // delayed because it needs project.version
parent?.let(::inheritConfigurationFrom)

fun setupPreprocessPlugin() {
    apply<PreprocessPlugin>()

    extensions.configure<PreprocessExtension> {
        vars.put("MC", mcVersion)
        vars.put("FABRIC", if (platform.isFabric) 1 else 0)
        vars.put("FORGE", if (platform.isForge) 1 else 0)
        vars.put("NEOFORGE", if (platform.isNeoForge) 1 else 0)
        vars.put("FORGELIKE", if (platform.isForgeLike) 1 else 0)
        vars.put("MODERN", if (platform.mcVersion >= 11300) 1 else 0)
    }
}

fun configureJavaVersion() {
    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(platform.javaVersion.majorVersion))
    }

    pluginManager.withPlugin("kotlin") {
        configure<KotlinJvmProjectExtension> {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(platform.javaVersion.majorVersion))
            }
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                // FIXME this should not be necessary because it is implied by the toolchain set above but IDEA seems to not
                //       recognize that and then errors when compiling
                jvmTarget = platform.javaVersion.toString()
            }
        }
    }
}

fun configureResources() {
    tasks.named<ProcessResources>("processResources") {
        // We define certain Kotlin/Groovy-style expansions to be used in the platform-specific mod metadata files
        val expansions = mutableMapOf(
            "name" to project.name,
            "version" to project.version,
            "java" to platform.javaVersion.majorVersion,
            "java_level" to "JAVA_" + platform.javaVersion.majorVersion,
            "mcVersionStr" to platform.mcVersionStr,
            // New forge needs the version to be exactly `${file.jarVersion}` to not explode in dev but that
            // also qualifies for replacement here, so we need to handle this ugly case.
            // And forge needs it to start with a number...
            "file" to mapOf("jarVersion" to project.version.toString().let { if (it[0].isDigit()) it else "0.$it" }),
        )

        // TODO is this required? are the FileCopyDetails not part of the input already?
        inputs.property("mod_version_expansions", expansions)

        filesMatching(listOf("mcmod.info", "META-INF/mods.toml", "mixins.*.json", "fabric.mod.json")) {
            expand(expansions)
        }

        // And exclude mod metadata files for other platforms
        if (!platform.isFabric) exclude("fabric.mod.json")
        if (!platform.isModLauncher) exclude("META-INF/mods.toml")
        if (!platform.isLegacyForge) exclude("mcmod.info")
    }
}

fun inheritConfigurationFrom(parent: Project) {
    // Inherit version from parent
    if (version == Project.DEFAULT_VERSION) {
        version = parent.version
    }

    // Inherit base archives name from parent, suffixed with the project name
    val parentBase = parent.extensions.findByType<BasePluginExtension>()
    val base = extensions.getByName("base") as BasePluginExtension
    if (parentBase != null) {
        base.archivesName.convention(parentBase.archivesName.map { "$it ${project.name}" })
    } else {
        base.archivesName.convention("${parent.name} ${project.name}")
    }

    afterEvaluate {
        tasks.withType<KotlinCompile> {
            kotlinOptions {
                if (moduleName == null && "-module-name" !in freeCompilerArgs) {
                    moduleName = project.findProperty("baseArtifactId")?.toString()
                            ?: parentBase?.archivesName?.orNull
                            ?: parent.name.lowercase()
                }
            }
        }
    }
}
