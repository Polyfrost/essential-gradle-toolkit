package org.polyfrost

apply(plugin = "org.polyfrost.defaults.repo")

pluginManager.withPlugin("java") { apply(plugin = "org.polyfrost.defaults.java") }
pluginManager.withPlugin("org.polyfrost.loom") { apply(plugin = "org.polyfrost.defaults.loom") }
