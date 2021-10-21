package plugins.openapi

import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.openapitools.generator.gradle.plugin.OpenApiGeneratorPlugin
import org.openapitools.generator.gradle.plugin.extensions.OpenApiGeneratorGenerateExtension
import plugins.PluginAdapter
import plugins.apply
import plugins.utils.loadLocalProperties
import plugins.utils.loadProperties
import java.util.*

class OpenApiKotlinJvmPlugin : Plugin<Project> by apply<OpenApiKotlinJvm>()

class OpenApiKotlinJvm(project: Project) : PluginAdapter(project) {
    private val generatorProps by lazy { loadProperties("generator").toMap() }
    private val localProps by lazy { loadLocalProperties().toMap() }

    override fun Project.apply() {
        pluginManager.apply(OpenApiGeneratorPlugin::class.java)

        val openApiKotlin = extensions.create("openApiKotlin", OpenApiGenerateKotlin::class.java, this)
        val openApiGenerate = the<OpenApiGeneratorGenerateExtension>()

        applyDefaults(openApiKotlin, openApiGenerate)
        registerTasks(openApiGenerate)

    }

    private fun Project.applyDefaults(openApiKotlin: OpenApiGenerateKotlin, openApiGenerate: OpenApiGeneratorGenerateExtension) {
        openApiGenerate.run {
            validateSpec.set(true)

            generatorName.set("kotlin")
            library.set("multiplatform")
            templateDir.set("${rootProject.projectDir}/kotlin-template")
            configFile.set("${rootProject.projectDir}/kotlin-template/libraries/multiplatform/config.yml")

            outputDir.set(openApiKotlin.artifactName.map { "${rootProject.buildDir}/generated/$it" })

            generatorProps["groupId"]?.let { groupId.set(it) }

            packageName.set(openApiKotlin.shortName.flatMap { shortName -> groupId.map { "$it.$shortName" } })
            apiPackage.set(packageName.map { "$it.api" })
            modelPackage.set(apiPackage.map { "$it.model" })

            typeMappings.putAll(
                "file" to "java.io.File",
                "date-time" to "java.time.Instant",
                "DateTime" to "java.time.Instant"
            )

            importMappings.putAll(
                "ObjectNode" to "kotlinx.serialization.json.JsonObject",
                "JsonNode" to "kotlinx.serialization.json.JsonElement"
            )

            configOptions.putAll(
                "dateLibrary" to "java8",
                "enumPropertyNaming" to "UPPERCASE"
            )
            configOptions.put("artifactId", openApiKotlin.artifactName)
            generatorProps["artifactVersion"]?.let { configOptions.put("artifactVersion", it) }

            additionalProperties.putAll(generatorProps + localProps)
        }
    }

    private fun Project.registerTasks(openApiGenerate: OpenApiGeneratorGenerateExtension) {
        tasks.register<Download>("downloadSpec") {
            dest("$buildDir")
        }

        tasks.register<Copy>("moveSources") {
            from(openApiGenerate.outputDir.map { "$it/src/commonMain" })
            into(openApiGenerate.outputDir.map { "$it/src/main" })
        }

        tasks.register<Delete>("deleteUnnecessarySources") {
            mustRunAfter("moveSources")
            delete.add(openApiGenerate.outputDir.map {
                listOf(
                    "$it/src/commonTest",
                    "$it/src/iosTest",
                    "$it/src/jsTest",
                    "$it/src/jvmTest",
                    "$it/src/commonMain"
                )
            })
        }

        tasks.named("openApiGenerate") {
            finalizedBy("moveSources", "deleteUnnecessarySources")
        }
    }

    override fun Project.afterEvaluated() {
        val openApiGenerate = the<OpenApiGeneratorGenerateExtension>()
        val openApiKotlin = the<OpenApiGenerateKotlin>()

        openApiGenerate.additionalProperties.put("pomName", openApiKotlin.name.get())
        openApiGenerate.additionalProperties.put("pomDescription", openApiKotlin.description.get())

        if (hasProperty("artifactVersion")) {
            openApiGenerate.configOptions.put("artifactVersion", properties["artifactVersion"] as String)
        }

        resolveSpecLocation(openApiKotlin, openApiGenerate)
    }

    private fun Project.resolveSpecLocation(openApiKotlin: OpenApiGenerateKotlin, openApiGenerate: OpenApiGeneratorGenerateExtension) {
        val downloadSpec = tasks.named<Download>("downloadSpec")
        val generateTask = tasks.named("openApiGenerate")

        if (hasProperty("specLocation")) {
            downloadSpec.configure { enabled = false }
            openApiGenerate.inputSpec.set(rootProject.projectDir.resolve(properties["specLocation"] as String).toString())
            return
        }

        if (hasProperty("specUrl")) {
            downloadSpec.configure { src(properties["specUrl"] as String) }
            generateTask.configure { dependsOn("downloadSpec") }
            openApiGenerate.inputSpec.set(downloadSpec.map { it.outputFiles.first().absolutePath })
            return
        }

        if (openApiGenerate.inputSpec.isPresent) {
            downloadSpec.configure { enabled = false }
            return
        }

        if (openApiKotlin.specLocation.isPresent) {
            downloadSpec.configure { enabled = false }
            openApiGenerate.inputSpec.set(rootProject.projectDir.resolve(openApiKotlin.specLocation.get()).toString())
            return
        }

        if (openApiKotlin.specsUrl.isPresent) {
            val urls = openApiKotlin.specsUrl.get()
            if (urls.isNotEmpty()) {
                downloadSpec.configure { src(urls) }
                generateTask.configure { dependsOn("downloadSpec") }
                openApiGenerate.inputSpec.set(downloadSpec.map { it.outputFiles.first().absolutePath })
                return
            }
        }

        throw IllegalStateException("Unable to determine input spec location")
    }
}

fun <K, V> MapProperty<K, V>.putAll(vararg entries: Pair<K, V>) = putAll(mapOf(*entries))

private fun Properties.toMap() = stringPropertyNames().associateWith { get(it) as String }
