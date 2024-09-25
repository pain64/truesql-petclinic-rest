plugins {
    id("java")
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "net.truej"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
	mavenLocal()
}

var trueSqlVersion = "3.0.0-beta2"

dependencies {
	annotationProcessor("net.truej:sql:$trueSqlVersion")
	annotationProcessor("org.postgresql:postgresql:42.7.3")

	testAnnotationProcessor("net.truej:sql:$trueSqlVersion")
	testAnnotationProcessor("org.postgresql:postgresql:42.7.3")

	implementation("net.truej:sql:$trueSqlVersion")
	implementation("org.jetbrains:annotations:24.0.0")
	implementation("org.postgresql:postgresql:42.7.3")
	implementation("com.zaxxer:HikariCP:5.1.0")

	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation("org.flywaydb:flyway-core:10.17.2")
	testImplementation("org.flywaydb:flyway-database-postgresql:10.17.2")
	testImplementation("org.testcontainers:postgresql:1.20.1")
	testImplementation("org.testcontainers:junit-jupiter:1.20.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
	options.compilerArgs.add("-Xplugin:TrueSql")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}