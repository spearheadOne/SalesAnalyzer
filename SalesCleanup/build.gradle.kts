plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("io.micronaut.application")
    id("io.micronaut.aot")
}

group = "org.abondar.experimental.sales.analyzer"

val testcontainersVersion: String by project
val awsLambdaEventsVersion: String by project


dependencies {
    implementation(project(":Data"))

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.aws:micronaut-function-aws-custom-runtime")
    implementation("io.micronaut.aws:micronaut-function-aws")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")


    implementation("com.amazonaws:aws-lambda-java-events:3.11.5")
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
    runtime("lambda_provided")
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
        replaceLogbackXml.set(false)
    }
}

graalvmNative {
    toolchainDetection.set(false)

    binaries {
        named("main") {
            mainClass.set("io.micronaut.function.aws.runtime.MicronautLambdaRuntime")

            imageName.set("SalesCleanup")

            buildArgs.addAll(
                "--verbose",
                "--no-fallback",
                "-march=compatibility",
                "--initialize-at-build-time=org.slf4j",
                "--initialize-at-build-time=ch.qos.logback",
                "-H:+ReportExceptionStackTraces",
            )
        }
    }
}

tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}

tasks.register<Exec>("dockerBuildNativeCli") {
    dependsOn("dockerBuildNative")
    workingDir = file("$buildDir/docker/native-main")
    commandLine("docker", "build", "-f", "DockerfileNative", "-t", "data:native", ".")
}