FROM bellsoft/liberica-openjdk-alpine:21 AS builder
WORKDIR /application
COPY . .
RUN --mount=type=cache,target=/root/.gradle chmod +x gradlew && ./gradlew clean :credit-application-service:bootJar -x test

FROM bellsoft/liberica-openjre-alpine:21
WORKDIR /application
COPY --from=builder /application/credit-application-service/build/libs/credit-application-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]