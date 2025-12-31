plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("io.micronaut.application")
    id("io.micronaut.aot")
    id("com.google.cloud.tools.jib")
}

group = "org.abondar.experimental.sales.analyzer"

val testcontainersVersion: String by project
val awsLambdaEventsVersion: String by project
val graalBaseImage: String by project
val imageArch: String by project
val imageOS: String by project

dependencies {
    implementation(project(":Data"))

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")

    implementation("io.micronaut.aws:micronaut-function-aws")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")


    implementation("com.amazonaws:aws-lambda-java-events:Ω")
    implementation("software.amazon.awssdk:s3")

    kapt("io.micronaut:micronaut-inject-java")

    testImplementation("io.micronaut.aws:micronaut-function-aws-test")
    testImplementation("org.testcontainers:localstack:$testcontainersVersion")

}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.abondar.experimental.sales.analyzer.cleanup.Main")
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.cleanup")
    }

    aot {
        optimizeServiceLoading.set(true)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
        replaceLogbackXml.set(false)
    }
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("SalesAnalyzerCleanup")
            buildArgs.add("--verbose")
            buildArgs.add(
                "--initialize-at-build-time=" +
                        "ch.qos.logback," +
                        "org.slf4j,"

            )
            runtimeArgs.add("-Dmicronaut.environments=local")
        }
    }
}

jib {
    from {
        image = graalBaseImage


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
                setFrom("build/native/nativeCompile")
                into = "/app"
            }
        }
        permissions = mapOf(
            "/app/SalesAnalyzerCleanup" to "755"
        )
    }

    container {
        entrypoint = listOf("/app/SalesAnalyzerCleanup")
    }
}

tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}

tasks.named("jib") {
    dependsOn("nativeCompile")
}

tasks.named("jibDockerBuild") {
    dependsOn("nativeCompile")
}