plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
    jacoco
    id("org.sonarqube") version "7.1.0.6387"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "MySawit Backend Project"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

checkstyle {
    toolVersion = "10.12.0"
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
}

tasks.withType<Checkstyle> {
    reports {
        xml.required.set(false)
        html.required.set(true)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    //implementation ("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly ("com.h2database:h2")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "advprog-2026-B6-project_MySawit-backend")
        property("sonar.organization", "advprog-2026-6-project")
    }
}