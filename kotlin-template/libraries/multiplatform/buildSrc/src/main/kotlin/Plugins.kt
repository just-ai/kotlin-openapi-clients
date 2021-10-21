import org.gradle.plugin.use.PluginDependenciesSpec

val PluginDependenciesSpec.`maven-publish-plugin` get() = id("com.justai.plugins.internal.publish")
