FROM oracle/graalvm-ce:20.2.0-java11 as graalvm
RUN gu install native-image

COPY . /home/app/micronaut-app
WORKDIR /home/app/micronaut-app

RUN native-image -cp build/libs/micronaut-app-*-all.jar

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
EXPOSE 8080
COPY --from=graalvm /home/app/micronaut-app/micronaut-app /app/micronaut-app
ENTRYPOINT ["/app/micronaut-app"]
