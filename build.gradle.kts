import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.noarg") version "1.9.23"
	id("maven-publish")
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.github.lolmageap"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	withSourcesJar()
	withJavadocJar()
}

repositories {
	mavenCentral()
}

noArg {
	annotation("org.springframework.data.mongodb.core.mapping.Document")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.3.4")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
	testImplementation("io.kotest:kotest-assertions-core:5.7.2")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootJar {
	archiveClassifier = ""
}

tasks.jar {
	archiveClassifier = ""
}

tasks.named("generateMetadataFileForMavenJavaPublication") {
	dependsOn(tasks.named("bootJar"))
}