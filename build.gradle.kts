import org.gradle.api.tasks.testing.logging.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    java
    application
    kotlinJvm
    kotlinKapt
    kotlinAllOpen
    kotlinxSerialization
    googleJib
    shadow
    benmanesVersions
    gitProperties
    `maven-publish`
    mavenPublishAuth
    gradleRelease
}

group = "dev.suresh"
description = "Micronaut data sample app!"

val gitUrl: String by project
val jdkVersion = JavaVersion.toVersion(14)

application {
    mainClassName = "dev.suresh.ApplicationKt"
}

java {
    sourceCompatibility = jdkVersion
    targetCompatibility = jdkVersion
}

gitProperties {
    gitPropertiesDir = "${project.buildDir}/resources/main/META-INF/${project.name}"
    customProperties["kotlin"] = Versions.kotlin
}

release {
    revertOnFail = true
}

jib {
    to {
        image = "micronaut-app"
    }
}

allOpen {
    annotation("io.micronaut.aop.Around")
}

kapt {
    arguments {
        arg("micronaut.processing.incremental", true)
        arg("micronaut.processing.annotations", "dev.suresh.*")
        arg("micronaut.processing.group", "dev.suresh")
        arg("micronaut.processing.module", "micronautApp")
    }
}

repositories {
    mavenCentral()
    jcenter()
}

// For dependencies that are needed for development only,
// creates a devOnly configuration and add it.
val devOnly: Configuration by configurations.creating

configurations {
    devOnly
}

tasks {

    withType<JavaCompile>().configureEach {
        options.apply {
            encoding = "UTF-8"
            isIncremental = true
            compilerArgs.addAll(
                listOf(
                    "--enable-preview",
                    "-Xlint:all",
                    "-parameters",
                    "--release",
                    jdkVersion.majorVersion
                )
            )
        }
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            verbose = true
            jvmTarget = "13"
            javaParameters = true
            freeCompilerArgs += listOf(
                "-progressive",
                "-Xjsr305=strict",
                "-Xjvm-default=enable",
                "-Xassertions=jvm",
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlinx.serialization.ImplicitReflectionSerializer"
            )
        }
    }

    // JUnit5
    test {
        useJUnitPlatform()
        jvmArgs("--enable-preview")
        classpath += devOnly
        testLogging {
            events = setOf(
                TestLogEvent.PASSED,
                TestLogEvent.FAILED,
                TestLogEvent.SKIPPED
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
        reports.html.isEnabled = true
    }

    // Release depends on publish.
    afterReleaseBuild {
        dependsOn(":publish")
    }

    // Configure Gradle Run.
    named<JavaExec>("run") {
        classpath += devOnly
        jvmArgs = listOf(
            "-XX:TieredStopAtLevel=1",
            "-Dcom.sun.management.jmxremote"
        )
        if (gradle.startParameter.isContinuous) {
            println("Enabling Micronaut Watch!")
            systemProperties(
                "micronaut.io.watch.restart" to "true",
                "micronaut.io.watch.enabled" to "true",
                "micronaut.io.watch.paths" to "src/main"
            )
        }
    }

    // Uber Jar
    shadowJar {
        mergeServiceFiles()
    }

    // Reproducible builds
    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    // Sources jar (lazy)
    val sourcesJar by registering(Jar::class) {
        //kotlin.sourceSets.main.get().kotlin
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }

    // Javadoc jar (lazy)
    val javadocJar by registering(Jar::class) {
        from(javadoc)
        archiveClassifier.set("javadoc")
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    // Disallow release candidates as upgradable versions from stable versions
    dependencyUpdates {
        rejectVersionIf { candidate.version.isNonStable && !currentVersion.isNonStable }
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }

    // Gradle Wrapper
    wrapper {
        gradleVersion = Versions.gradle
        distributionType = Wrapper.DistributionType.BIN
    }

    // Default task
    defaultTasks("clean", "tasks", "--all")
}

dependencies {

    // Micronaut annotation processors
    kapt(platform(Deps.micronautBom))
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")
    kapt("io.micronaut:micronaut-graal")
    kapt("io.micronaut.data:micronaut-data-processor")

    compileOnly(platform(Deps.micronautBom))
    compileOnly("org.graalvm.nativeimage:svm")

    implementation(enforcedPlatform(Deps.kotlinBom))
    implementation(platform(Deps.micronautBom))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("javax.annotation:javax.annotation-api")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-http-client")
    //implementation("io.micronaut.configuration:micronaut-flyway")
    //implementation("io.micronaut.configuration:micronaut-jdbc-hikari")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("com.h2database:h2")

    kaptTest(platform(Deps.micronautBom))
    kaptTest("io.micronaut:micronaut-inject-java")

    testImplementation(platform(Deps.micronautBom))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Enable native file watch only on dev mode.
    devOnly(platform(Deps.micronautBom))
    devOnly("io.micronaut:micronaut-runtime-osx")
    devOnly("net.java.dev.jna:jna")
    devOnly("io.methvin:directory-watcher")
}

publishing {
    repositories {
        maven {
            url = uri("$buildDir/repo")
        }
    }

    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.shadowJar.get())

            pom {
                packaging = "jar"
                description.set(project.description)
                inceptionYear.set("2020")
                url.set(gitUrl)

                developers {
                    developer {
                        id.set("sureshg")
                        name.set("Suresh")
                        email.set("email@suresh.dev")
                        organization.set("Suresh")
                        organizationUrl.set("https://suresh.dev")
                    }
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                scm {
                    url.set(gitUrl)
                    tag.set("HEAD")
                    connection.set("scm:git:$gitUrl.git")
                    developerConnection.set("scm:git:$gitUrl.git")
                }

                issueManagement {
                    system.set("github")
                    url.set("$gitUrl/issues")
                }
            }
        }
    }
}
