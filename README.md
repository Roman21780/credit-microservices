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

## для запуска без докера

powershell
Set-ExecutionPolicy RemoteSigned -Scope Process  # Разрешить запуск скриптов
.\start-infra.ps1

## команды в терминале
### Окно 1:
cd C:\kafka
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

### Окно 2:
.\bin\windows\kafka-server-start.bat .\config\server.properties
или
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

### Окно 3:
# Для RabbitMQ
Get-Service RabbitMQ
Start-Service RabbitMQ
"C:\Program Files\RabbitMQ Server\rabbitmq_server-3.12.0\sbin\rabbitmqctl.bat" status

bash
chmod +x start-infra.sh
./start-infra.sh

## После запуска всех сервисов можно отправлять запросы:

создать топик в Kafka

kafka-topics.sh --create --topic credit-applications --bootstrap-server localhost:9092

проверка, что топик создан

kafka-topics.sh --list --bootstrap-server localhost:9092

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