plugins {
    `openapi-generator-kotlin-jvm`
}

openApiKotlin {
    specsUrl.add("https://help.just-ai.com/caila_direct.yml")
    artifactName.set("caila-api")
    shortName.set("caila")
    name.set("Caila OpenAPI client")
    description.set("Caila OpenAPI client")
}