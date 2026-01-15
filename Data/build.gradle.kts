plugins {
    kotlin("jvm")
    id("io.micronaut.application")

}

group = "org.abondar.experimental.sales.analyzer"


dependencies {
    implementation("io.micronaut.serde:micronaut-serde-api")
}
