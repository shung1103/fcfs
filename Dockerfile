FROM openjdk:21-jdk
CMD ["./gradlew", "clean", "build"]
WORKDIR /usr/src/app
COPY build/libs/fcfs-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]