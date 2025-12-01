import io.micronaut.gradle.docker.MicronautDockerfile
import io.micronaut.gradle.docker.NativeImageDockerfile
import org.gradle.kotlin.dsl.named

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("io.micronaut.application")
    id("io.micronaut.aot")
}

group = "org.abondar.experimental.sales.analyzer"
version = "0.1.0"

val kotlinCoroutinesVersion: String by project
val testcontainersVersion: String by project

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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    kapt("io.micronaut:micronaut-inject-java")

    testImplementation("io.micronaut.aws:micronaut-function-aws-test")
    testImplementation("org.testcontainers:localstack:$testcontainersVersion")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.abondar.experimental.sales.analyzer.ingester.ApplicationKt")
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.ingester")
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

graalvmNative {
    binaries {
        named("main") {
            imageName.set("SalesIngester")
            buildArgs.add("--verbose")
            buildArgs.add(
                "--initialize-at-build-time=" +
                        "ch.qos.logback," +
                        "org.slf4j," +
                        "kotlin.coroutines.intrinsics.CoroutineSingletons"
            )
            runtimeArgs.add("-Dmicronaut.environments=local")
        }
    }
}

tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}

tasks.named<NativeImageDockerfile>("dockerfileNative") {
    args("-Dmicronaut.environments=aws")
}

tasks.dockerBuildNative {
    images.add("sales-ingester:${project.version}")
}