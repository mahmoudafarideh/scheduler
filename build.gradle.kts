plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    alias(libs.plugins.springframework.boot) apply(false)
    alias(libs.plugins.spring.dependecy.management)
}

group = "m.a"
version = "0.0.1-SNAPSHOT"
description = "Scheduler"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.reflect)
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.security.test)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
