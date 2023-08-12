package org.polyfrost

apply(plugin = "org.polyfrost.defaults.repo")

pluginManager.withPlugin("java") { apply(plugin = "org.polyfrost.defaults.java") }
pluginManager.withPlugin("gg.essential.loom") { apply(plugin = "org.polyfrost.defaults.loom") }
