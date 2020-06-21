import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    micronautApp
    kotlinJvm
    kotlinKapt
    kotlinxSerialization
    shadow
    googleJib
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

micronaut {
    version(Versions.micronaut)
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

repositories {
    mavenCentral()
    jcenter()
}

tasks {

    withType<JavaCompile>().configureEach {
        options.apply {
            encoding = "UTF-8"
            isIncremental = true
            compilerArgs.addAll(listOf("--release", jdkVersion.majorVersion))
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
                "-Xassertions=jvm"
            )
        }
    }

    // JUnit5
    test {
        useJUnitPlatform()
    }

    shadowJar {
        mergeServiceFiles()
    }

    // Release depends on publish.
    afterReleaseBuild {
        dependsOn(":publish")
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
    implementation(enforcedPlatform(Deps.kotlinBom))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.flyway:micronaut-flyway")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("ch.qos.logback:logback-classic")
    compileOnly("org.graalvm.nativeimage:svm")

    kapt(platform(Deps.micronautBom))
    kapt("io.micronaut:micronaut-graal")
    kapt("io.micronaut.data:micronaut-data-processor")

    kaptTest("io.micronaut:micronaut-inject-java")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
