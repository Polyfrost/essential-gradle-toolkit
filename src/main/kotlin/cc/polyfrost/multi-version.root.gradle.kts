package cc.polyfrost

import cc.polyfrost.gradle.util.checkJavaVersion

plugins {
    id("com.replaymod.preprocess-root")
}

checkJavaVersion(JavaVersion.VERSION_16)
