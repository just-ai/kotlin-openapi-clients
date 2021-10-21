plugins {
    `openapi-generator-kotlin-jvm`
}

openApiKotlin {
    specsUrl.addAll(
        "https://app.jaicp.com/dialer/static/openapi/calls.yml",
        "https://app.jaicp.com/dialer/static/openapi/common.yml"
    )
    artifactName.set("calls-api")
    shortName.set("calls")
    name.set("Calls OpenAPI client")
    description.set("Calls OpenAPI client")
}

openApiGenerate {
    // hack to disable incorrect imports
    importMappings.putAll(
        mapOf(
            "OneOfLessThanPhoneErrorResponseCommaCampaignErrorResponseCommaDialerPriorityErrorResponseGreaterThan" to "kotlin.*",
            "OneOfLessThanPhoneErrorResponseCommaCampaignErrorResponseGreaterThan" to "kotlin.*"
        )
    )
}
