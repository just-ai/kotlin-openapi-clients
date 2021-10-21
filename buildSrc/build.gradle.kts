plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2")
}

val kotlinVersion = "1.4.32"
val reflectVersion = "1.4.32"

dependencies {
    api("de.undercouch:gradle-download-task:4.1.2")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.0")
    implementation(gradleApi())
    implementation("org.openapitools:openapi-generator-gradle-plugin:5.2.0")
}