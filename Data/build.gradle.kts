import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm")
    id("io.micronaut.application")
    id("com.google.protobuf") version "0.9.4"
}

group = "org.abondar.experimental.sales.analyzer"
version = "0.1.0"

dependencies {
    implementation("io.micronaut.serde:micronaut-serde-api")
    api("io.grpc:grpc-netty-shaded:1.66.0")
    api("com.google.protobuf:protobuf-kotlin:3.25.1")
    api("io.grpc:grpc-stub:1.60.0")
    api("io.grpc:grpc-protobuf:1.60.0")
    api("io.grpc:grpc-kotlin-stub:1.4.1")
    api("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }

    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.60.0"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
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
