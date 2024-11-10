plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.noarg") version "1.9.23"
    id("maven-publish")
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-data-mongodb:3.3.4")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")
}

noArg {
    annotation("org.springframework.data.mongodb.core.mapping.Document")
    annotation("com.kmsl.dsl.annotation.Projection")
    annotation("com.kmsl.dsl.annotation.EmbeddedDocument")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    archiveClassifier = ""
}