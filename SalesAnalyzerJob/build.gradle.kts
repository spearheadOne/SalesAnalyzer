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
    implementation(project(":Data"))

    implementation("io.micronaut:micronaut-context")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.liquibase:micronaut-liquibase")

    runtimeOnly("org.postgresql:postgresql:42.7.3")

    implementation("software.amazon.awssdk:secretsmanager")
    implementation("software.amazon.awssdk:kinesis")
    implementation("software.amazon.kinesis:amazon-kinesis-client:2.6.0")
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:cloudwatch")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    implementation("org.mybatis:mybatis:3.5.19")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")


    kapt("io.micronaut:micronaut-inject-java")
    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("org.testcontainers:localstack:1.19.7")
}

kotlin {
    jvmToolchain(21)
}

application { mainClass.set("org.abondar.experimental.sales.analyzer.job.Main") }

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.job.*")
    }

    aot {
        optimizeServiceLoading.set(true)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
        replaceLogbackXml.set(false)
    }
}

tasks.shadowJar {
    archiveBaseName.set("sales-analyzer-job")
    archiveClassifier.set("all")
}

tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}

tasks.named<NativeImageDockerfile>("dockerfileNative") {
    args("-Dmicronaut.environments=docker")
}

tasks.named<MicronautDockerfile>("dockerfile") {
    args("-Dmicronaut.environments=docker")
}
