plugins {
    kotlin("jvm") version "2.0.21" apply false
    kotlin("kapt") version "2.0.21" apply false
    kotlin("plugin.allopen") version "2.0.21" apply false

    id("io.micronaut.application") version "4.5.4" apply false
    id("io.micronaut.aot") version "4.5.4" apply false
    id("com.google.cloud.tools.jib") version "3.5.1" apply false
}

subprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")


    group = "org.abondar.experimental.sales.analyzer"

    version = "0.5.8.1"

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        val awsSdkVersion: String by project
        val logbackVersion: String by project
        val mockitoVersion: String by project
        val mockitoKotlinVersion: String by project
        val kotlinCoroutinesTestVersion: String by project
        val testcontainersVersion: String by project


        add("implementation", "ch.qos.logback:logback-classic:$logbackVersion")

        add("implementation", platform("software.amazon.awssdk:bom:${awsSdkVersion}"))
        add("implementation", "software.amazon.awssdk:regions")
        add("implementation", "software.amazon.awssdk:auth")
        add("implementation", "io.micronaut.serde:micronaut-serde-jackson")
        add("kapt", "io.micronaut.serde:micronaut-serde-processor")
        add("runtimeOnly", "org.yaml:snakeyaml")

        add("testImplementation", "io.micronaut.test:micronaut-test-junit5")
        add("testImplementation", "org.mockito:mockito-core:${mockitoVersion}")
        add("testImplementation", "org.mockito:mockito-junit-jupiter:${mockitoVersion}")
        add("testImplementation", "org.mockito.kotlin:mockito-kotlin:${mockitoKotlinVersion}")
        add("testImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-test:${kotlinCoroutinesTestVersion}")
        add("testImplementation", "org.testcontainers:junit-jupiter:${testcontainersVersion}")
        add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine")
    }

    plugins.withId("com.google.cloud.tools.jib") {

        val serviceEnvName = "${project.name.uppercase()}_ECR_REPO"
        val ecrRepoUrl: String? = System.getenv(serviceEnvName)

        extensions.configure<com.google.cloud.tools.jib.gradle.JibExtension> {
            to {
                image = if (ecrRepoUrl.isNullOrBlank()) {
                    "${project.name.lowercase()}:${project.version}"
                } else {
                    "$ecrRepoUrl:${project.version}"
                }
            }
        }
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}

