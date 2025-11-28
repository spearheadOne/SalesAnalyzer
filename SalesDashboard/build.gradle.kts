import io.micronaut.gradle.docker.MicronautDockerfile
import io.micronaut.gradle.docker.NativeImageDockerfile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")

    application
    id("com.gradleup.shadow")
    id("io.micronaut.application")
    id("io.micronaut.aot")
}

group = "org.abondar.experimental.sales.analyzer"
version = "0.1.0"

dependencies {
    implementation(project(":Data"))
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.openapi:micronaut-openapi-annotations")
    implementation("io.micronaut.jms:micronaut-jms-sqs")

    implementation("org.mybatis:mybatis:3.5.19")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")

    runtimeOnly("org.postgresql:postgresql:42.7.3")

    annotationProcessor("io.micronaut.openapi:micronaut-openapi")

    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-management")
    kapt("io.micronaut:micronaut-inject-java")

    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
    testImplementation("io.micronaut:micronaut-http-client")
}

kotlin {
    jvmToolchain(21)
}

application { mainClass.set("org.abondar.experimental.sales.analyzer.dashboard.ApplicationKt") }

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.dashboard.*")
    }
}


val frontendDir = file("${projectDir}/dashboard-frontend")
val frontendBuildDir = file("${projectDir}/src/main/resources/public")

tasks.register<Exec>("yarnInstall") {
    workingDir = frontendDir
    commandLine("yarn", "install")
    inputs.file("${frontendDir}/package.json")
    inputs.file("${frontendDir}/yarn.lock")
    outputs.dir("${frontendDir}/node_modules")
}

tasks.register<Exec>("yarnBuild") {
    dependsOn("yarnInstall")
    workingDir = frontendDir
    commandLine("yarn", "build")
    inputs.dir(frontendDir)
    outputs.dir(frontendBuildDir)
}

tasks.classes {
    dependsOn("yarnBuild")
}

tasks.inspectRuntimeClasspath {
    dependsOn("yarnBuild")
}

tasks.processResources {
    dependsOn("yarnBuild")
}

tasks.shadowJar {
    archiveBaseName.set("sales-analyzer-dashboard")
    archiveClassifier.set("all")
    dependsOn("yarnBuild")
}

tasks.named<JavaExec>("run") {
    dependsOn("yarnBuild")
    systemProperty("micronaut.environments", "local")
}

tasks.named<NativeImageDockerfile>("dockerfileNative") {
    args("-Dmicronaut.environments=aws")
}

tasks.named<MicronautDockerfile>("dockerfile") {
    args("-Dmicronaut.environments=aws")
}