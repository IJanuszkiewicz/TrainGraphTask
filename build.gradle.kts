plugins {
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.16"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.1.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.10")
}

tasks.test {
    useJUnitPlatform()
}

benchmark {
    targets {
        register("main")
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

application {
    mainClass.set("MainKt")
}