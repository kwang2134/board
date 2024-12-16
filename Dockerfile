FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY build/libs/board-0.0.1-SNAPSHOT.jar app.jar

LABEL authors="kwang"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]