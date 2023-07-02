package cc.polyfrost

apply(plugin = "cc.polyfrost.defaults.repo")

pluginManager.withPlugin("java") { apply(plugin = "cc.polyfrost.defaults.java") }
pluginManager.withPlugin("gg.essential.loom") { apply(plugin = "cc.polyfrost.defaults.loom") }
