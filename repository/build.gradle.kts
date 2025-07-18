plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization)
    `java-test-fixtures`
}

// Move to build plugin
group = "io.github.monolithic.invoicer.repository.api"
version = "0.0.1"

dependencies {
    implementation(projects.models)
    implementation(projects.foundation.cache)
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.datetime)
    implementation(libs.bundles.exposed)
    implementation(libs.postgres)

    implementation(libs.kodein.server)
    implementation(libs.kotlin.datetime)

    // Fixtures
    testFixturesImplementation(testFixtures(projects.models))
    testFixturesImplementation(libs.kotlin.datetime)

    // Test
    testImplementation(kotlin("test"))
    testImplementation(testFixtures(projects.foundation.cache))
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(testFixtures(projects.models))
}
