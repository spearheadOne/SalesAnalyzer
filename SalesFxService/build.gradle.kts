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

    implementation("io.micronaut.grpc:micronaut-grpc-server-runtime")
    implementation("io.grpc:grpc-netty-shaded:1.66.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    kapt("io.micronaut:micronaut-inject-java")
    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation("io.grpc:grpc-testing:1.66.0")
    testImplementation("io.micronaut.grpc:micronaut-grpc-client-runtime")
}

kotlin {
    jvmToolchain(21)
}

application {  mainClass.set("io.micronaut.grpc.server.GrpcEmbeddedServer") }

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.fx*")
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