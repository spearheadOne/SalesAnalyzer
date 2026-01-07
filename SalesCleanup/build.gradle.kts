plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("com.google.devtools.ksp")
    id("io.micronaut.application")
}

group = "org.abondar.experimental.sales.analyzer"

val testcontainersVersion: String by project
val awsLambdaEventsVersion: String by project


dependencies {
    implementation(project(":Data"))

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.aws:micronaut-function-aws")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
    implementation("com.amazonaws:aws-lambda-java-events:$awsLambdaEventsVersion")
    implementation("software.amazon.awssdk:s3")

    ksp("io.micronaut:micronaut-inject-java")

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
}


tasks.named<JavaExec>("run") {
    systemProperty("micronaut.environments", "local")
}