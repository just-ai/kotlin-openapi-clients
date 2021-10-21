plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2")
}

dependencies {
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.0")
    implementation(gradleApi())
}