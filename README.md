### [Micronaut](https://micronaut.io/index.html) Data JDBC Sample App

[![GitHub Workflow Status][shieldio_img]][gha_url] 
[![Micronaut][mnaut_img]][mnaut_jar] 
[![Kotlin release][kt_img]][kt_url] 
[![OpenJDK Version][java_img]][java_url] 
[![Style guide][sty_img]][sty_url]

 * Build & Run
     ```bash        
     $ ./gradlew run -t
     ```
 * Run Tests
     ```bash
     $ ./gradlew test
     ```
 * Build Executable Jar
     ```bash
     $ ./gradlew build 
     $  java -jar build/libs/micronaut-app-0.1.0-all.jar  
     ```

 * Build Native Image
     ```bash
      $ sdk u java 20.2.0.r11-grl
      $ ./gradlew nativeImage 
     ```
   
[java_url]: https://jdk.java.net/
[java_img]: https://img.shields.io/badge/OpenJDK-jdk--11-red?logo=java&style=for-the-badge&logoColor=red

[kt_url]: https://github.com/JetBrains/kotlin/releases/latest
[kt_img]: https://img.shields.io/github/release/JetBrains/kotlin.svg?label=Kotlin&logo=kotlin&style=for-the-badge

[sty_url]: https://kotlinlang.org/docs/reference/coding-conventions.html
[sty_img]: https://img.shields.io/badge/style-Kotlin--Official-40c4ff.svg?style=for-the-badge&logo=kotlin&logoColor=40c4ff 

[mnaut_mvn]: https://search.maven.org/search?q=g:io.micronaut
[mnaut_jar]: https://search.maven.org/remote_content?g=io.micronaut&a=micronaut-http-server-netty&v=LATEST
[mnaut_img]: https://img.shields.io/maven-central/v/io.micronaut/micronaut-runtime?color=orange&label=micronaut&logo=apache-rocketmq&logoColor=orange&style=for-the-badge

[gha_url]: https://github.com/sureshg/micronaut-app/actions
[gha_img]: https://github.com/sureshg/micronaut-app/workflows/Gradle%20Build/badge.svg?branch=master                         
[shieldio_img]: https://img.shields.io/github/workflow/status/sureshg/micronaut-app/Gradle%20Build?color=green&label=Build&logo=Github-Actions&logoColor=green&style=for-the-badge
