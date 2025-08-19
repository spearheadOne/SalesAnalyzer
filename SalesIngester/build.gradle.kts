plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("io.micronaut.application")
}

group = "org.abondar.experimental.sales.analyzer"
version = "0.1.0"


dependencies {
    implementation(project(":Data"))

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")

    implementation("io.micronaut.aws:micronaut-function-aws")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")

    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:kinesis")
    implementation("software.amazon.awssdk:s3-event-notifications")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    runtimeOnly("org.yaml:snakeyaml")

    testImplementation("io.micronaut.aws:micronaut-function-aws-test")
    testImplementation("org.testcontainers:localstack:1.19.7")
}

application {
    mainClass.set("org.abondar.experimental.sales.analyzer.ingester.ApplicationKt")
}

kotlin {
    jvmToolchain(21)
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.ingester.*")
    }
}

tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}