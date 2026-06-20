plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
    id("org.openapi.generator") version "7.9.0"
}

group = "com.diningplate"
version = property("version") as String

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

val openApiSpec: Configuration by configurations.creating

repositories {
    mavenLocal()
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.2")
    }
}

dependencies {
    openApiSpec("com.diningplate:order-rest-api-spec:${property("orderRestApiSpecVersion")}")

    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("io.swagger.core.v3:swagger-core-jakarta:2.2.22")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.projectlombok:lombok")
}

val extractOpenApiSpec by tasks.registering(Copy::class) {
    group = "openapi"
    description = "Extracts order-api.yaml from the spec JAR"

    val specJar = openApiSpec.elements.map { it.single().asFile }
    from(specJar.map { zipTree(it) }) {
        include("openapi/order-api.yaml")
        eachFile { relativePath = RelativePath(true, name) }
        includeEmptyDirs = false
    }
    into(layout.buildDirectory.dir("openapi-spec"))
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set(layout.buildDirectory.file("openapi-spec/order-api.yaml").get().asFile.absolutePath)
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath)

    apiPackage.set("com.diningplate.orderservice.api")
    modelPackage.set("com.diningplate.orderservice.api.model")
    invokerPackage.set("com.diningplate.orderservice.api")

    configOptions.set(
        mapOf(
            "useSpringBoot3" to "true",
            "delegatePattern" to "true",
            "useTags" to "true",
            "serializationLibrary" to "jackson",
            "enumPropertyNaming" to "UPPERCASE",
            "gradleBuildFile" to "false",
            "exceptionHandler" to "false",
        )
    )
}

springBoot {
    mainClass.set("com.diningplate.orderservice.OrderServiceApplicationKt")
}

tasks.named("openApiGenerate") { dependsOn(extractOpenApiSpec) }
tasks.named("compileKotlin") { dependsOn("openApiGenerate") }

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }

    sourceSets {
        main {
            kotlin.srcDir(layout.buildDirectory.dir("generated/openapi/src/main/kotlin"))
        }
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
