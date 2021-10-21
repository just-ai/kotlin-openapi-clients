package plugins.openapi

import org.gradle.api.Project
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property

open class OpenApiGenerateKotlin(project: Project) {

    /**
     * Path to local spec location
     */
    val specLocation = project.objects.property<String>()

    /**
     * URL of remote spec locations
     */
    val specsUrl = project.objects.listProperty<String>()

    /**
     * Name of generated client artifact
     */
    val artifactName = project.objects.property<String>()

    /**
     * Short name of generated client. Used as default package name.
     */
    val shortName = project.objects.property<String>()

    /**
     * Full name of generated client. Used as maven publication name.
     */
    val name = project.objects.property<String>()

    /**
     * Description of generated client. Used as maven publication description.
     */
    val description = project.objects.property<String>()
}
