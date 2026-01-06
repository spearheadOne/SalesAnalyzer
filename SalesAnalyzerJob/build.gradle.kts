plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")

    application
    id("io.micronaut.application")
}

group = "org.abondar.experimental.sales.analyzer"


val mybatisVersion: String by project
val mybatisJsr310Version: String by project
val postgresqlVersion: String by project
val kinesisClientVersion: String by project
val kotlinCoroutinesVersion: String by project
val testcontainersVersion: String by project

dependencies {
    implementation(project(":Data"))

    implementation("io.micronaut:micronaut-context")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.liquibase:micronaut-liquibase")
    implementation("io.micronaut.grpc:micronaut-grpc-client-runtime")

    implementation("software.amazon.awssdk:secretsmanager")
    implementation("software.amazon.awssdk:kinesis")
    implementation("software.amazon.kinesis:amazon-kinesis-client:$kinesisClientVersion")
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:cloudwatch")
    implementation("software.amazon.awssdk:sqs")

    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.mybatis:mybatis:$mybatisVersion")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:$mybatisJsr310Version")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    kapt("io.micronaut:micronaut-inject-java")
    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.testcontainers:localstack:$testcontainersVersion")
}

application {
    mainClass.set("org.abondar.experimental.sales.analyzer.job.Main")
}

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.job.*")
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}

tasks.named("build") {
    dependsOn("installDist")
}
