package org.polyfrost

import org.polyfrost.gradle.util.checkJavaVersion

plugins {
    id("dev.deftu.gradle.preprocess-root")
}

checkJavaVersion(JavaVersion.VERSION_21)
