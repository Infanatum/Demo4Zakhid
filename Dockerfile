FROM openjdk:11-jre-slim

LABEL authors="aleks"

WORKDIR /app

COPY build/libs/Demo4Zakhid-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
