package org.polyfrost

import org.polyfrost.gradle.util.checkJavaVersion

plugins {
    id("com.replaymod.preprocess-root")
}

checkJavaVersion(JavaVersion.VERSION_16)
