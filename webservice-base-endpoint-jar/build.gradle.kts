plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.dynamicwebservice"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.a09090443:base-spring-boot-starter:3.2.5.4")
    implementation("io.github.a09090443:db-spring-boot-starter:3.2.5.1")
    implementation("net.sf.jasperreports:jasperreports:6.20.6")
    implementation("com.github.librepdf:openpdf:2.0.2")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
