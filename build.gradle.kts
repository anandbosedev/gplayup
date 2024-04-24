plugins {
    application
    kotlin("jvm") version "1.9.23"
}

group = "com.anandbose"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev20240418-2.0.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
application {
    mainClass = "com.anandbose.MainKt"
}