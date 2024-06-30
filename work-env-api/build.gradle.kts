import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.5"
	id("io.spring.dependency-management") version "1.1.3"

	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.8.22"
	kotlin("kapt") version "1.8.22"
	kotlin("plugin.allopen") version "1.8.22"
}



group = "com.sindory"
version = "0.0.2-SNAPSHOT"
val queryDslVersion = "5.0.0" // QueryDSL Version Setting

java {
 	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation ("com.querydsl:querydsl-jpa:${queryDslVersion}:jakarta")
	kapt("com.querydsl:querydsl-apt:${queryDslVersion}:jakarta") // kapt 사용
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation ("io.github.microutils:kotlin-logging:3.0.5")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly ("mysql:mysql-connector-java:8.0.29")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.ninja-squad:springmockk:3.0.1")

	testImplementation("io.kotest:kotest-runner-junit5:5.4.2") // kotlin junit 처럼 쓸 수 있는 Spec 들이 정의 됨
	testImplementation("io.kotest:kotest-assertions-core:5.4.2") // shouldBe... etc 와같이 Assertions 의 기능을 제공
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2") // spring boot test 를 위해서 추가

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
