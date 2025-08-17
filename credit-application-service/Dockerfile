FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/credit-application-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]