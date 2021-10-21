<h1 align="center">Kotlin OpenAPI clients</h1>

This project contains OpenAPI generators (Kotlin/JVM, Ktor, kotlinx.serialization) for Reporter API, CAILA API and Calls API.

## Generating clients

The following command will generate `reporter-api`
```
./gradlew :reporter-api openApiGenerate
```

In order to generate all clients at once run the following command:
```
./gradlew openApiGenerate
```

### Available customization

- `generator.properties`
```
groupId=com.justai.openapi
artifactVersion=1.11.0-SNAPSHOT
kotlinVersion=1.4.32
serializationVersion=1.0.1
ktorVersion=1.5.1
```

- `build.gradle.kts`
```kotlin
openApiKotlin {
    specLocation.set("/path/to/spec")
    specsUrl.add("https://example.org/spec.yml")
    artifactName.set("reporter-api")
    shortName.set("reporter")
    name.set("Just AI Reporter API client")
    description.set("Just AI Reporter API client")
}
```

- Command Line Arguments
```
-PartifactVersion - defines version of generated artifact
-PspecLocation - defines location of local spec, takes precedence over specUrl
-PspecUrl - defines URL of remote spec. NOTE: multiple URLs are not supported
```
**WARN: do not use `specLocation` or `specUrl` when generating multiple clients at once**

Example:
```
./gradlew :reporter-api -PspecUrl=https://example.org/reporter.yml -PartifactVersion=1.11.1
```

## Publishing

All generated clients are ready for publishing to Maven Central
```
cd build/generated/reporter-api && ./gradlew publishAllPublicationsToMavenCentral
```
