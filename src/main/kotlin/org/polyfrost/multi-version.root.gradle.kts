package org.polyfrost

import org.polyfrost.gradle.util.checkJavaVersion

plugins {
    id("xyz.deftu.gradle.preprocess-root")
}

checkJavaVersion(JavaVersion.VERSION_16)
