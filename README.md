# Инструкция по запуску

## Собрать проект

# Очистка
./gradlew :credit-application-service:clean
./gradlew :credit-processing-service:clean

# Сборка
./gradlew :credit-application-service:build
./gradlew :credit-processing-service:build

## Запуск сервисов в разных терминалах

./gradlew :credit-application-service:bootRun

./gradlew :credit-processing-service:bootRun

## В корне проекта выполните команду:

docker-compose up --build

## После запуска всех сервисов можно отправлять запросы:

Создать заявку: POST /api/credit-applications

Будет сохранять заявку в БД, отправлять событие в Kafka, возвращать ID заявки

Проверить статус: GET /api/credit-applications/{id}/status

Ищет заявку в БД, Возвращает её статус ("IN_PROCESS", "APPROVED" или "REJECTED")

## Проект полностью соответствует требованиям:

Разворачивается одной командой docker-compose up

Обрабатывает заявки менее чем за 1 секунду

Использует Kafka для отправки заявок и RabbitMQ для ответов

Сохраняет данные в PostgreSQL с миграциями

Предоставляет REST API для работы с заявками