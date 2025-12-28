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

val mybatisVersion: String by project
val mybatisJsr310Version: String by project
val postgresqlVersion: String by project
val testcontainersVersion: String by project


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
    implementation("software.amazon.awssdk:secretsmanager")


    implementation("org.mybatis:mybatis:$mybatisVersion")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:$mybatisJsr310Version")
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")

    annotationProcessor("io.micronaut.openapi:micronaut-openapi")

    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-management")
    kapt("io.micronaut:micronaut-inject-java")

    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
    testImplementation("io.micronaut:micronaut-http-client")
}

application {
    mainClass.set("org.abondar.experimental.sales.analyzer.dashboard.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dmicronaut.environments=local")
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

val baseImage: String by project
val imageArch: String by project
val imageOS: String by project

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

    to {
        image = "sales-dashboard:${project.version}"
    }

    extraDirectories {
        paths {
            path {
                setFrom(
                    layout.buildDirectory
                        .dir("install/SalesDashboard")
                        .get()
                        .asFile
                        .toPath()
                )
                into = "/app"
            }
        }

        permissions = mapOf(
            "/app/SalesDashboard" to "755"
        )
    }

    container {
        entrypoint = listOf("/app/bin/SalesDashboard")

        environment = mapOf(
            "MICRONAUT_ENVIRONMENTS" to "aws"
        )

        ports = listOf("9024")
    }
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

tasks.named("build") {
    dependsOn("installDist")
}

tasks.named("jib") {
    dependsOn("installDist")
}

tasks.named("jibDockerBuild") {
    dependsOn("installDist")
}