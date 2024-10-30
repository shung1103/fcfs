FROM gradle:jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle :gateway-service:build -build test --no-daemon

FROM openjdk:21-jdk

WORKDIR /app

COPY /build/libs/*.jar app.jar

COPY /src/main/resources/application.yml /app/application.yml

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application.yml"]