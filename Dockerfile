FROM openjdk:8-jdk-alpine

EXPOSE 8000

ADD /build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
