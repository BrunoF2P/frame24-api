plugins {
    java
    war
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.frame24"
version = "0.0.1-SNAPSHOT"
description = "Frame 24"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springModulithVersion"] = "2.0.0"
ext["netty.version"] = "4.2.7.Final"
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.0")
        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:4.0.0-M1")
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
    dependencies {
        // Force Spring Modulith 2.0.0 to prevent Spring Cloud BOM from downgrading
        dependency("org.springframework.modulith:spring-modulith-events-api:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-starter-core:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-starter-jpa:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-events-core:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-events-jpa:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-events-jackson:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-core:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-api:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-apt:2.0.0")
        dependency("org.springframework.modulith:spring-modulith-moments:2.0.0")
    }
}

dependencies {
    // Dependências principais do Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-h2console")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // OpenAPI / Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.0.0")

    // Redis Cache
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("io.lettuce:lettuce-core")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // JWT - jjwt
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    compileOnly("org.jspecify:jspecify:1.0.0")

    // Spring Modulith
    implementation("org.springframework.modulith:spring-modulith-events-api")
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jpa")

    // WebSocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // Flyway para PostgreSQL
    implementation("org.flywaydb:flyway-database-postgresql")

    // Lombok para reduzir boilerplate
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    // Ferramentas de desenvolvimento
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Banco de dados
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    // Para o Minio (com Spring Cloud AWS ou SDK Minio)
    implementation("io.awspring.cloud:spring-cloud-aws-starter")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
    implementation("software.amazon.awssdk:s3:2.40.2")

    // Para o Mailhog
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Testes
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "--enable-native-access=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.nio=ALL-UNNAMED",
        "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED"
    )
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    jvmArgs = listOf(
        "--enable-native-access=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.nio=ALL-UNNAMED"
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf(
        "--enable-native-access=ALL-UNNAMED"
    )
}
