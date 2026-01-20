import sun.jvmstat.monitor.MonitoredVmUtil.mainClass

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("com.google.devtools.ksp")
    id("io.micronaut.application")
    id("com.gradleup.shadow")
}

group = "org.abondar.experimental.sales.analyzer"


val kotlinCoroutinesVersion: String by project
val kotlinVersion: String by project


dependencies {
    developmentOnly("io.micronaut:micronaut-http-server-netty")

    implementation(project(":Data"))

    implementation("io.micronaut.aws:micronaut-function-aws")
    implementation("io.micronaut.aws:micronaut-aws-lambda-events-serde")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
    implementation("io.micronaut:micronaut-http-client-jdk")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:kinesis")
    implementation("io.micronaut.serde:micronaut-serde-api")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("software.amazon.awssdk:s3-event-notifications")


    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    ksp("io.micronaut.serde:micronaut-serde-processor")
    ksp("io.micronaut:micronaut-inject-java")
    ksp("io.micronaut:micronaut-http-validation")

    testImplementation("io.micronaut.aws:micronaut-function-aws-test")
    testImplementation("org.testcontainers:testcontainers-localstack")
}

kotlin {
    jvmToolchain(21)
}

micronaut {
    runtime("lambda_java")
    testRuntime("junit5")
}

application {
    mainClass.set("org.abondar.experimental.sales.analyzer.ingester.input.SalesIngesterHandler")
}


tasks.named<JavaExec>("run") {
    application {
        mainClass.set("org.abondar.experimental.sales.analyzer.ingester.ApplicationKt")
    }
    systemProperty("micronaut.environments", "local")
}

