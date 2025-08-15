
plugins {
    kotlin("jvm") version "2.0.21" apply false
    kotlin("kapt") version "2.0.21" apply false
    kotlin("plugin.allopen") version "2.0.21" apply false

    id("io.micronaut.application") version "4.5.4" apply false
    id("io.micronaut.test-resources") version "4.5.4" apply false
    id("io.micronaut.aot") version "4.5.4" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("com.gradleup.shadow") version "8.3.0" apply false
}

subprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")


    group = "org.abondar.experimental.sales.analyzer"

    version = "0.1.0"

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        add("implementation","ch.qos.logback:logback-classic:1.5.13")

        add("implementation",platform("software.amazon.awssdk:bom:2.32.23"))
        add("implementation","software.amazon.awssdk:regions")
        add("implementation","software.amazon.awssdk:auth")
        add("implementation" ,"io.micronaut.serde:micronaut-serde-jackson")
        add ("kapt", "io.micronaut.serde:micronaut-serde-processor")

        add("testImplementation", "io.micronaut.test:micronaut-test-junit5")
        add("testImplementation", "org.mockito:mockito-core:5.12.0")
        add("testImplementation", "org.mockito:mockito-junit-jupiter:5.12.0")
        add("testImplementation", "org.mockito.kotlin:mockito-kotlin:5.3.1")
        add("testImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
        add("testImplementation", "org.testcontainers:junit-jupiter:1.19.7")

        add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine")

    }
}


