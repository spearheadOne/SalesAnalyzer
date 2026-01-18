plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")

    application
    id("com.google.devtools.ksp")
    id("io.micronaut.application")
}

group = "org.abondar.experimental.sales.analyzer"


val mybatisVersion: String by project
val mybatisJsr310Version: String by project
val postgresqlVersion: String by project
val testcontainersExtVersion: String by project
val awsSdkVersion: String by project

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
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation(platform("software.amazon.awssdk:bom:$awsSdkVersion"))
    implementation("software.amazon.awssdk:secretsmanager")

    implementation("org.mybatis:mybatis:$mybatisVersion")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:$mybatisJsr310Version")
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    runtimeOnly("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")

    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.openapi:micronaut-openapi")
    ksp("io.micronaut.data:micronaut-data-processor")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    ksp("io.micronaut:micronaut-management")
    ksp("io.micronaut:micronaut-inject-java")

    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
    testImplementation("io.micronaut:micronaut-http-client")
}

application {
    mainClass.set("org.abondar.experimental.sales.analyzer.dashboard.ApplicationKt")
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.abondar.experimental.sales.analyzer.dashboard.*")
    }
}

kotlin {
    jvmToolchain(21)
}

val frontendDir = file("$projectDir/dashboard-frontend")
val frontendBuildDir = file("$projectDir/src/main/resources/public")
val runningInIdea = System.getProperty("idea.active") == "true"
val skipFrontend: Boolean = runningInIdea || (project.findProperty("skipFrontend") == "true")

tasks.register<Exec>("yarnInstall") {
    workingDir = frontendDir
    commandLine("yarn", "install")
    inputs.file("$frontendDir/package.json")
    inputs.file("$frontendDir/yarn.lock")
    outputs.dir("$frontendDir/node_modules")

    onlyIf { !skipFrontend }
}

tasks.register<Exec>("yarnBuild") {
    dependsOn("yarnInstall")
    workingDir = frontendDir
    commandLine("yarn", "build")
    inputs.dir(frontendDir)
    outputs.dir(frontendBuildDir)

    onlyIf { !skipFrontend }
}

if (!skipFrontend) {
    tasks.classes {
        dependsOn("yarnBuild")
    }

    tasks.inspectRuntimeClasspath {
        dependsOn("yarnBuild")
    }

    tasks.processResources {
        dependsOn("yarnBuild")
    }


    tasks.named<JavaExec>("run") {
        dependsOn("yarnBuild")
        systemProperty("micronaut.environments", "local")
    }

}
