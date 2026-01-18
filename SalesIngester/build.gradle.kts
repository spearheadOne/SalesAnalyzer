plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("com.google.devtools.ksp")
    id("io.micronaut.application")
    id("io.micronaut.aot")
}

group = "org.abondar.experimental.sales.analyzer"


val kotlinCoroutinesVersion: String by project
val testcontainersExtVersion: String by project

val kotlinVersion: String by project
val graalVmImage: String by project
val lambdaImage: String by project


dependencies {
    developmentOnly("io.micronaut:micronaut-http-server-netty")

    implementation(project(":Data"))

    implementation("io.micronaut.aws:micronaut-function-aws-custom-runtime")
    implementation("io.micronaut.aws:micronaut-aws-lambda-events-serde")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
    implementation("io.micronaut:micronaut-http-client-jdk")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("software.amazon.awssdk:s3") {
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
    }
    implementation("software.amazon.awssdk:kinesis") {
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
    }
    implementation("software.amazon.awssdk:s3-event-notifications")

    implementation("software.amazon.awssdk:url-connection-client")

    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    ksp("io.micronaut:micronaut-inject-java")
    ksp("io.micronaut:micronaut-http-validation")

    testImplementation("io.micronaut.aws:micronaut-function-aws-test")
    testImplementation("org.testcontainers:testcontainers-localstack")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.abondar.experimental.sales.analyzer.ingester.SalesIngesterRuntime"
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
        mainClass.set("org.abondar.experimental.sales.analyzer.ingester.ApplicationKt")
    }
    systemProperty("micronaut.environments", "local")
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}
