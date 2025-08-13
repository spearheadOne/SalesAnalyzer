
plugins {
    kotlin("jvm") version "2.0.21" apply false
    kotlin("kapt") version "2.0.21" apply false
    kotlin("plugin.allopen") version "2.0.21" apply false

    id("io.micronaut.application") version "4.5.4" apply false
    id("io.micronaut.test-resources") version "4.5.4" apply false
    id("io.micronaut.aot") version "4.5.4" apply false
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
}


