plugins {
    kotlin("jvm") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
    kotlin("plugin.allopen") version "2.0.21" apply false

    id("io.micronaut.application") version "4.6.1" apply false
    id("io.micronaut.aot") version "4.6.1" apply false
}

version = "0.8.2.13"

subprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.google.devtools.ksp")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")


    group = "org.abondar.experimental.sales.analyzer"

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        val logbackVersion: String by project
        val mockitoVersion: String by project
        val mockitoKotlinVersion: String by project
        val kotlinCoroutinesTestVersion: String by project
        val testcontainersVersion: String by project


        add("runtimeOnly", "org.yaml:snakeyaml")
        add("runtimeOnly", "ch.qos.logback:logback-classic:$logbackVersion")


        add("testImplementation", platform("org.testcontainers:testcontainers-bom:${testcontainersVersion}"))
        add("testImplementation", "io.micronaut.test:micronaut-test-junit5")
        add("testImplementation", "org.mockito:mockito-core:${mockitoVersion}")
        add("testImplementation", "org.mockito:mockito-junit-jupiter:${mockitoVersion}")
        add("testImplementation", "org.mockito.kotlin:mockito-kotlin:${mockitoKotlinVersion}")
        add("testImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-test:${kotlinCoroutinesTestVersion}")
        add("testImplementation", "org.testcontainers:testcontainers-junit-jupiter")
        add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine")
    }

}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

