package plugins.utils

import org.gradle.api.Plugin
import org.gradle.api.Project
import plugins.PluginAdapter
import java.util.*

val Project.isRoot get() = parent == null

inline fun <reified P : Plugin<*>> Project.applySafely(): Boolean {
    return if (plugins.hasPlugin(P::class.java)) {
        false
    } else {
        plugins.apply(P::class.java)
        true
    }
}

fun PluginAdapter.loadLocalProperties(): Properties = loadProperties("local")

fun PluginAdapter.loadProperties(name: String): Properties = Properties().apply {
    project.rootProject.file("$name.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
}