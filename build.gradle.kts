plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "cc.polyfrost"
version = "0.1.17"

java.withSourcesJar()

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.polyfrost.cc/releases")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    api(libs.archloom)
    implementation(libs.archloomPack200)

    compileOnly(libs.kotlin.gradlePlugin)
    implementation(libs.kotlinx.binaryCompatibilityValidator)
    implementation(libs.proguard) {
        exclude(group = "org.jetbrains.kotlin")
    }
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    api(libs.preprocessor)
    implementation(libs.asm)
    implementation(libs.guava)
    implementation(libs.kotlinx.metadata.jvm)
}

publishing {
    repositories {
        if (project.hasProperty("releasesUsername") && project.hasProperty("releasesPassword")) {
            maven("https://repo.polyfrost.cc/releases") {
                name = "polyfrost"
                credentials {
                    username = project.property("releasesUsername").toString()
                    password = project.property("releasesPassword").toString()
                }
            }
        }
    }
}
