### [Micronaut](https://micronaut.io/index.html) Data JDBC Sample App

 * Build & Run
     ```bash        
     $ ./gradlew run --continuous --watch-fs
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
      $ sdk u java 20.1.0.r11-grl
      $ ./gradlew nativeImage 
     ```