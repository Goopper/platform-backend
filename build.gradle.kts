import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "top.goopper"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    mavenLocal()
    mavenCentral()
}

extra["springCloudVersion"] = "2023.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // spring-jdbc
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    // jwt-api
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    // jwt-impl
    implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
    // jwt-jackson
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.5")
    // user-agent-utils
    implementation("eu.bitwalker:UserAgentUtils:1.21")
    // https://mvnrepository.com/artifact/org.ktorm/ktorm-core
    implementation("org.ktorm:ktorm-core:3.6.0")
    // https://mvnrepository.com/artifact/org.ktorm/ktorm-support-mysql
    implementation("org.ktorm:ktorm-support-mysql:3.6.0")
    // https://mvnrepository.com/artifact/org.ktorm/ktorm-jackson
    implementation("org.ktorm:ktorm-jackson:3.6.0")
    // spring-cloud-starter-consul
    implementation("org.springframework.cloud:spring-cloud-starter-consul-config")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    // bootstrap
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    // https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-s3
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.688") {
        // exclude commons-logging to avoid conflict with spring-boot
        exclude(group = "commons-logging", module = "commons-logging")
    }
    // https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api
    implementation("javax.xml.bind:jaxb-api:2.3.1")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}