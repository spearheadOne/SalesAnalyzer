plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")

    application
    id("io.micronaut.application")
    id("com.google.cloud.tools.jib")
}

group = "org.abondar.experimental.sales.analyzer"
version = "0.1.0"

val grpcVersion: String by project
val kotlinCoroutinesVersion: String by project
val baseImage: String by project
val imageArch: String by project
val imageOS: String by project


dependencies {
    implementation(project(":Data"))

    implementation("io.micronaut:micronaut-context")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")

    implementation("io.micronaut.grpc:micronaut-grpc-server-runtime")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    kapt("io.micronaut:micronaut-inject-java")
    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation("io.grpc:grpc-testing:$grpcVersion")
    testImplementation("io.micronaut.grpc:micronaut-grpc-client-runtime")
}

application {
    mainClass.set("org.abondar.experimental.sales.analyzer.fx.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dmicronaut.environments=local")
}

kotlin {
    jvmToolchain(21)
}


micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.fx*")
    }
}

jib {
    from {
        image = baseImage


        platforms {
            platform {
                architecture = imageArch
                os = imageOS
            }
        }
    }

    extraDirectories {
        paths {
            path {
                setFrom(
                    layout.buildDirectory
                        .dir("install/SalesFxService")
                        .get()
                        .asFile
                        .toPath()
                )
                into = "/app"
            }
        }
        permissions = mapOf(
            "/app/SalesFxService" to "755"
        )
    }

    container {
        entrypoint = listOf("/app/bin/SalesFxService")
        ports = listOf("9028")
    }
}

tasks.named("build") {
    dependsOn("installDist")
}

tasks.named("jib") {
    dependsOn("installDist")
}

tasks.named("jibDockerBuild") {
    dependsOn("installDist")
}
