plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "org.polyfrost"
version = "0.2.9"

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
    maven("https://repo.polyfrost.org/releases")
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
    implementation(libs.shadow)
    api(libs.preprocessor)
    implementation(libs.asm)
    implementation(libs.guava)
    implementation(libs.kotlinx.metadata.jvm)
    //implementation(libs.minotaur)
    //implementation(libs.cursegradle)
    //implementation(libs.github.release)
}

publishing {
    repositories {
        if (project.hasProperty("releasesUsername") && project.hasProperty("releasesPassword")) {
            maven("https://repo.polyfrost.org/releases") {
                name = "polyfrost"
                credentials {
                    username = project.property("releasesUsername").toString()
                    password = project.property("releasesPassword").toString()
                }
            }
        }
    }
}
