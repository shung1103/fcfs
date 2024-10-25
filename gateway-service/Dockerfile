FROM gradle:jdk21 AS builder

WORKDIR /app

COPY . .

#RUN gradle :gateway-service:build -x test --no-daemon

FROM openjdk:21-jdk

WORKDIR /app

COPY --from=builder /app/gateway-service/build/libs/*.jar app.jar

COPY --from=builder /app/gateway-service/src/main/resources/application.yml /app/application.yml

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application.yml"]