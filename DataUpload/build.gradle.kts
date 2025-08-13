plugins {
    id("java")
}

group = "org.abondar.experimental.sales.analyzer"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":Data"))

   // testImplementation("io.micronaut.test:micronaut-test-junit5")
}

