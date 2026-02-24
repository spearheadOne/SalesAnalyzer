plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")

    application
    id("com.google.devtools.ksp")
    id("io.micronaut.application")
}

group = "org.abondar.experimental.sales.analyzer"


val mybatisVersion: String by project
val mybatisJsr310Version: String by project
val postgresqlVersion: String by project
val kinesisClientVersion: String by project
val kotlinCoroutinesVersion: String by project
val testcontainersExtVersion: String by project
val awsSdkVersion: String by project

dependencies {
    implementation(project(":Data"))
    implementation(project(":GrpcData"))

    implementation("io.micronaut:micronaut-context")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.liquibase:micronaut-liquibase")
    implementation("io.micronaut.grpc:micronaut-grpc-client-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation(platform("software.amazon.awssdk:bom:$awsSdkVersion"))
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

    ksp("io.micronaut:micronaut-inject-java")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    kspTest("io.micronaut:micronaut-inject-java")

    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.testcontainers:testcontainers-localstack")
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
    jvmToolchain(25)
}

tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}

tasks.named("build") {
    dependsOn("installDist")
}
