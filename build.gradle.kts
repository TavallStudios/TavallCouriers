import java.util.zip.ZipFile

plugins {
    base
}

group = "org.tavall"
extra["versionTagPrefix"] = "TavallCouriers"
extra["fallbackVersion"] = "0.0.1"
apply(from = "gradle/git-version.gradle.kts")
version = extra["gitVersion"] as String

val springBootVersion = "4.0.2"
val springFrameworkVersion = "7.0.3"
val jacksonVersion = "2.20.1"
val junitVersion = "6.0.2"
val googleGenAiVersion = "1.38.0"
val zxingVersion = "3.5.4"
val pdfBoxVersion = "3.0.6"
val assertJVersion = "3.27.7"
val mockitoVersion = "5.21.0"
val testcontainersVersion = "1.21.4"

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
        withSourcesJar()
    }

    repositories {
        mavenCentral()
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = 25
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters", "--enable-preview"))
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        jvmArgs("--enable-preview")
    }

    tasks.withType<Jar>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    val verifyThinJar = tasks.register("verifyThinJar") {
        dependsOn(tasks.named("jar"))
        val archive = tasks.named<Jar>("jar").flatMap { it.archiveFile }
        inputs.file(archive)
        doLast {
            val forbidden = listOf(
                "com/fasterxml/",
                "com/google/",
                "jakarta/",
                "org/apache/",
                "org/springframework/",
            )
            ZipFile(archive.get().asFile).use { jar ->
                val embedded = jar.entries().asSequence().map { it.name }
                    .firstOrNull { entry -> forbidden.any(entry::startsWith) }
                check(embedded == null) { "Third-party class embedded in thin JAR: $embedded" }
            }
        }
    }

    tasks.named("check") {
        dependsOn(verifyThinJar)
    }

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = project.name
            }
        }
        repositories {
            val token = providers.environmentVariable("GITHUB_TOKEN")
            if (token.isPresent) {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/TavallStudios/TavallCouriers")
                    credentials {
                        username = providers.environmentVariable("GITHUB_ACTOR").orNull
                        password = token.get()
                    }
                }
            }
        }
    }
}

project(":gemini-api") {
    dependencies {
        "api"("com.google.genai:google-genai:$googleGenAiVersion")
        "testImplementation"("org.junit.jupiter:junit-jupiter:$junitVersion")
        "testImplementation"("org.assertj:assertj-core:$assertJVersion")
        "testImplementation"("org.mockito:mockito-core:$mockitoVersion")
        "testImplementation"("org.mockito:mockito-junit-jupiter:$mockitoVersion")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher:$junitVersion")
    }
}

project(":internal-courier-api") {
    dependencies {
        "api"(project(":gemini-api"))
        "api"("com.google.genai:google-genai:$googleGenAiVersion")
        "api"("org.springframework:spring-context:$springFrameworkVersion")
        "api"("org.springframework:spring-jdbc:$springFrameworkVersion")
        "api"("org.springframework.boot:spring-boot-starter-security:$springBootVersion")
        "api"("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
        "api"("org.apache.pdfbox:pdfbox:$pdfBoxVersion")
        "api"("com.google.zxing:core:$zxingVersion")
        "api"("com.google.zxing:javase:$zxingVersion")
        "api"(platform("com.fasterxml.jackson:jackson-bom:$jacksonVersion"))
        "api"("com.fasterxml.jackson.core:jackson-annotations")
        "api"("com.zaxxer:HikariCP:7.0.2")
        "runtimeOnly"("org.postgresql:postgresql:42.7.9")
        "testImplementation"("org.junit.jupiter:junit-jupiter:$junitVersion")
        "testImplementation"("org.assertj:assertj-core:$assertJVersion")
        "testImplementation"("org.mockito:mockito-core:$mockitoVersion")
        "testImplementation"("org.mockito:mockito-junit-jupiter:$mockitoVersion")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher:$junitVersion")
    }
}

project(":spring-webview") {
    apply(plugin = "application")

    extensions.configure<SourceSetContainer> {
        named("main") {
            java.setSrcDirs(listOf("main/java"))
            resources.setSrcDirs(listOf("main/resources"))
        }
        named("test") {
            java.setSrcDirs(listOf("test/java"))
            resources.setSrcDirs(listOf("test/resources"))
        }
    }

    extensions.configure<JavaApplication> {
        mainClass = "org.tavall.couriers.TavallCouriersApplication"
        applicationDefaultJvmArgs = listOf("--enable-preview")
    }

    dependencies {
        "implementation"(project(":internal-courier-api"))
        "implementation"(project(":gemini-api"))
        "implementation"("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
        "implementation"("org.springframework.boot:spring-boot-starter-security:$springBootVersion")
        "implementation"("org.springframework.boot:spring-boot-starter-oauth2-client:$springBootVersion")
        "implementation"("org.springframework.boot:spring-boot-starter-validation:$springBootVersion")
        "implementation"("org.springframework.boot:spring-boot-starter-data-jdbc:$springBootVersion")
        "implementation"("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
        "implementation"("org.springframework.boot:spring-boot-starter-thymeleaf:$springBootVersion")
        "implementation"("com.zaxxer:HikariCP:7.0.2")
        "runtimeOnly"("org.postgresql:postgresql:42.7.9")
        "runtimeOnly"("org.springframework.boot:spring-boot-devtools:$springBootVersion")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
        "testImplementation"("org.springframework.boot:spring-boot-starter-data-jpa-test:$springBootVersion")
        "testImplementation"("org.testcontainers:junit-jupiter:$testcontainersVersion")
        "testImplementation"("org.testcontainers:postgresql:$testcontainersVersion")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher:$junitVersion")
    }

    tasks.named<Jar>("jar") {
        archiveFileName = "tavall-couriers.jar"
        manifest {
            attributes["Main-Class"] = "org.tavall.couriers.TavallCouriersApplication"
        }
    }
}

val stageDistribution = tasks.register<Sync>("stageDistribution") {
    val web = project(":spring-webview")
    dependsOn(web.tasks.named("jar"))
    into(layout.projectDirectory.dir("distribution/server"))
    from(web.tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
        rename { "application.jar" }
    }
    into("libs") {
        from(web.configurations.getByName("runtimeClasspath"))
    }
}

tasks.named("assemble") {
    dependsOn(stageDistribution)
}
