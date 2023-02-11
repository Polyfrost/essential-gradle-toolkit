plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "cc.polyfrost"
version = "0.1.27"

java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16

tasks.withType(JavaCompile::class).configureEach {
    options.encoding = "UTF-8"
    options.release.set(16)
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "16"
    }
}

java.withSourcesJar()

repositories {
    maven("https://repo.polyfrost.cc/releases")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    api(libs.archloom)

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
    implementation(libs.machete)
    //implementation(libs.minotaur)
    //implementation(libs.cursegradle)
    //implementation(libs.github.release)
}

publishing {
    repositories {
        if (project.hasProperty("releasesUsername") && project.hasProperty("releasesPassword")) {
            maven("https://repo.polyfrost.cc/releases") {
                name = "poly"
                credentials(PasswordCredentials::class)
            }
        }
    }
}
