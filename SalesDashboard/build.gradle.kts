import io.micronaut.gradle.docker.MicronautDockerfile
import io.micronaut.gradle.docker.NativeImageDockerfile
import org.gradle.kotlin.dsl.named

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")

    application
    id("com.gradleup.shadow")
    id("io.micronaut.application")
    id("io.micronaut.aot")
}

group = "org.abondar.experimental.sales.analyzer"
version = "0.1.0"

dependencies {
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("org.mybatis:mybatis:3.5.19")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")

    runtimeOnly("org.postgresql:postgresql:42.7.3")

    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-management")

    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
}

kotlin {
    jvmToolchain(21)
}

application { mainClass.set("org.abondar.experimental.sales.analyzer.dashboard.ApplicationKt") }

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.dashboard")
    }
}

tasks.shadowJar {
    archiveBaseName.set("sales-analyzer-dashboard")
    archiveClassifier.set("all")
}

tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}

tasks.named<NativeImageDockerfile>("dockerfileNative") {
    args("-Dmicronaut.environments=aws")
}

tasks.named<MicronautDockerfile>("dockerfile") {
    args("-Dmicronaut.environments=aws")
}