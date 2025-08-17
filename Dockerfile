# Базовый образ с Java 21
FROM eclipse-temurin:21-jre-alpine

# Рабочая директория в контейнере
WORKDIR /app

# Аргумент для передачи пути к JAR (будет задан в docker-compose)
ARG JAR_PATH

# Копируем JAR-файл в контейнер
COPY ${JAR_PATH} app.jar

# Точка входа для запуска Spring Boot приложения
ENTRYPOINT ["java", "-jar", "app.jar"]