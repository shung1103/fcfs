FROM gradle:jdk21 AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew	#gradlew 실행 권한 부여
RUN ./gradlew bootJar	#gradlew를 통해 실행 가능한 jar파일 생성

FROM openjdk:21-jdk

WORKDIR /app

COPY /build/libs/*.jar app.jar

COPY /src/main/resources/application.yml /app/application.yml

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application.yml"]
