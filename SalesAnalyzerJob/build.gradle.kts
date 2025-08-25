plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")

    application
    id("com.gradleup.shadow")
    id("io.micronaut.application")
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

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.2")

    implementation("org.mybatis:mybatis:3.5.19")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")

    compileOnly("org.apache.flink:flink-core:2.1.0")
    compileOnly("org.apache.flink:flink-streaming-java:2.1.0")
    compileOnly("org.apache.flink:flink-clients:2.1.0")
    implementation("org.apache.flink:flink-connector-kinesis:5.0.0-1.20")

    kapt("io.micronaut:micronaut-inject-java")
    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation("org.testcontainers:postgresql:1.20.1")
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
}

tasks.shadowJar {
    archiveBaseName.set("sales-analyzer-job")
    archiveClassifier.set("all")
    minimize {
        exclude(dependency("org.apache.flink:.*"))
    }
}