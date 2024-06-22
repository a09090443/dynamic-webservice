plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.scb"
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
    implementation("io.github.a09090443:base-spring-boot-starter:3.2.5.3")
    implementation("io.github.a09090443:web-service-spring-boot-starter:3.2.5.2")
    implementation("net.sf.jasperreports:jasperreports:6.20.6")
    implementation("com.github.librepdf:openpdf:2.0.2")
    implementation("org.eclipse.persistence:eclipselink:3.0.4")
    implementation(files("src/main/lib/webservice-base-endpoint.jar"))

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
