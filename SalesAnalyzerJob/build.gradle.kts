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

val flink = "1.19.1"

dependencies {
    implementation(project(":Data"))

    implementation("io.micronaut:micronaut-context")

    implementation("io.micronaut.liquibase:micronaut-liquibase:6.0.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    runtimeOnly("org.postgresql:postgresql:42.7.3")

    implementation("software.amazon.awssdk:secretsmanager")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

    implementation("org.mybatis:mybatis:3.5.15")

//    compileOnly("org.apache.flink:flink-streaming-java:$flink")
//    compileOnly("org.apache.flink:flink-clients:$flink")
//    compileOnly("org.apache.flink:flink-connector-kinesis:$flink")
//    compileOnly("org.apache.flink:flink-connector-jdbc:$flink")
//
//    runtimeOnly("org.apache.flink:flink-streaming-java:$flink")
//    runtimeOnly("org.apache.flink:flink-clients:$flink")
//    runtimeOnly("org.apache.flink:flink-connector-kinesis:$flink")
//    runtimeOnly("org.apache.flink:flink-connector-jdbc:$flink")


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