import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.plugin.allopen") version "1.5.21"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.5.21"
	id("org.springframework.boot") version "2.6.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

allOpen {
	annotations("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embedabble")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
	environment = mapOf(
		"BPE_DELIM_JAVA_TOOL_OPTIONS" to " ",
		"BPE_APPEND_JAVA_TOOL_OPTIONS" to "-Dfile.encoding=UTF-8"
	)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation(fileTree("libs"))
	implementation("com.google.code.gson:gson:2.8.6")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
//	implementation("org.hibernate:hibernate-core:6.0.1.Final")
	implementation("edu.stanford.nlp:stanford-corenlp:4.4.0")
	implementation(group = "edu.stanford.nlp", name = "stanford-corenlp", version = "4.4.0", classifier = "models")
	implementation(group = "edu.stanford.nlp", name = "stanford-corenlp", version = "4.4.0")
	implementation(group = "com.vladmihalcea", name = "hibernate-types-52", version = "1.0.0")

	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation(kotlin("script-runtime"))
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

springBoot {
	mainClass.set( "com.example.demo.DemoApplicationKt")
}