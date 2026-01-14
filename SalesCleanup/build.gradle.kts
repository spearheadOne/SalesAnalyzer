plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("com.google.devtools.ksp")
    id("io.micronaut.application")
    id("io.micronaut.aot")
}

group = "org.abondar.experimental.sales.analyzer"

val testcontainersVersion: String by project
val awsLambdaEventsVersion: String by project
val kotlinVersion: String by project
val graalVmImage: String by project
val lambdaImage: String by project
val awsSdkVersion: String by project

dependencies {

    implementation("io.micronaut.aws:micronaut-aws-lambda-events-serde")
    implementation("io.micronaut.aws:micronaut-function-aws-custom-runtime")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")

    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation(platform("software.amazon.awssdk:bom:$awsSdkVersion"))

    implementation("software.amazon.awssdk:s3") {
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
    }
    implementation("software.amazon.awssdk:url-connection-client")

    ksp("io.micronaut:micronaut-inject-java")
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")

    testImplementation("io.micronaut.aws:micronaut-function-aws-test")
    testImplementation("org.testcontainers:localstack:$testcontainersVersion")

}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "io.micronaut.function.aws.runtime.MicronautLambdaRuntime"
}

micronaut {
    runtime("lambda_java")
    testRuntime("junit5")

    aot {
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

tasks.named<JavaExec>("run") {
    application {
        mainClass.set("org.abondar.experimental.sales.analyzer.cleanup.Main")
    }

    systemProperty("micronaut.environments", "local")
}

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
    graalImage = graalVmImage
    baseImage = lambdaImage

}
