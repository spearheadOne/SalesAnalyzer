plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")

    application
    id("io.micronaut.application")
}

group = "org.abondar.experimental.sales.analyzer"

val grpcVersion: String by project
val kotlinCoroutinesVersion: String by project


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


tasks.named("build") {
    dependsOn("installDist")
}
