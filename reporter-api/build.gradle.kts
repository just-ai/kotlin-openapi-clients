plugins {
    `openapi-generator-kotlin-jvm`
}

openApiKotlin {
    specsUrl.add("https://app.jaicp.com/reporter/static/specs/reporter-public.yml")
    artifactName.set("reporter-api")
    shortName.set("reporter")
    name.set("Reporter OpenAPI client")
    description.set("Reporter OpenAPI client")
}
