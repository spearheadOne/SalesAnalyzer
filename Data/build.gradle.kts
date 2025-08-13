plugins {
    kotlin("jvm")
    id("io.micronaut.application")
}

group = "org.abondar.experimental.sales.analyzer"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.micronaut.serde:micronaut-serde-api")
}

tasks.test {
    useJUnitPlatform()
}