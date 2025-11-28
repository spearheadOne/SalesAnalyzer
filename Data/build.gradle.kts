import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm")
    id("io.micronaut.application")
    id("com.google.protobuf") version "0.9.4"
}

group = "org.abondar.experimental.sales.analyzer"
version = "0.1.0"

val grpcVersion: String by project
val protobufVersion: String by project
val grpcKotlinVersion: String by project
val javaxAnnotationVersion: String by project
val grpcNettyVersion: String by project

dependencies {
    implementation("io.micronaut.serde:micronaut-serde-api")
    api("io.grpc:grpc-netty-shaded:$grpcNettyVersion")
    api("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    api("io.grpc:grpc-stub:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    api("javax.annotation:javax.annotation-api:$javaxAnnotationVersion")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

sourceSets {
    main {
        proto {
            srcDir("src/main/proto")
        }
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
