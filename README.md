# Цепочка обработки:

## credit-application-service:

Создает заявку со статусом IN_PROCESS

Отправляет событие в Kafka

## credit-processing-service:

Получает событие из Kafka

Рассчитывает решение

Отправляет результат в RabbitMQ

## credit-application-service:

Получает результат из RabbitMQ

Обновляет статус заявки


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
$env:KAFKA_HEAP_OPTS = "-Xmx512M -Xms512M"
.\bin\windows\kafka-server-start.bat .\config\server.properties
проверка работы в другом окне:
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

# Просмотр сообщений в топике
.\bin\windows\kafka-console-consumer.bat --topic credit-applications --from-beginning --bootstrap-server localhost:9092

# Отправка тестового сообщения
.\bin\windows\kafka-console-producer.bat --topic credit-applications --bootstrap-server localhost:9092


### Окно 3:
# Для RabbitMQ
# В отдельном окне терминала
cd "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.1.3\sbin"
rabbitmq-server.bat
проверка:
Get-Service RabbitMQ
Start-Service RabbitMQ
"C:\Program Files\RabbitMQ Server\rabbitmq_server-3.12.0\sbin\rabbitmqctl.bat" status
включение веб-интерфейса
.\rabbitmq-plugins.bat enable rabbitmq_management

# Команды RabbitMQ
# Просмотр списка очередей
rabbitmqctl.bat list_queues

# Просмотр подключений
rabbitmqctl.bat list_connections

# Управление плагинами (например, для веб-интерфейса)
rabbitmq-plugins.bat enable rabbitmq_management

# Логи
Get-Content -Path "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.1.3\var\log\rabbitmq\*.log" -Tail 20

## Статус сервисов
docker ps | grep -E 'kafka|rabbitmq'
# Проверка запущенных контейнеров:
docker ps --filter "name=kafka"
docker ps --filter "name=rabbit"
# Проверка логов:
docker logs <container_name>

# Для Kafka (если установлен как сервис):
Get-Service -Name "kafka" -ErrorAction SilentlyContinue

# Для RabbitMQ (стандартное имя сервиса):
Get-Service -Name "RabbitMQ" -ErrorAction SilentlyContinue

# Если сервисы не найдены, проверьте все сервисы:
Get-Service | Where-Object { $_.DisplayName -like "*kafka*" -or $_.DisplayName -like "*rabbit*" }


# Проверка порта Kafka (9092):
Test-NetConnection -ComputerName localhost -Port 9092

# Проверка порта RabbitMQ (5672):
Test-NetConnection -ComputerName localhost -Port 5672

# Если нужно проверить доступность сервиса (HTTP API RabbitMQ на 15672):
Test-NetConnection -ComputerName localhost -Port 15672

# Проверка процессов
# Поиск процессов Java (Kafka/Zookeeper):
Get-Process java | Where-Object { $_.CommandLine -like "*kafka*" }

# Для RabbitMQ (если запущен как процесс):
Get-Process erl -ErrorAction SilentlyContinue  # RabbitMQ на Erlang

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


# Публикация тестового сообщения
& "C:\Program Files\RabbitMQ Server\rabbitmq_server-3.12.0\sbin\rabbitmqadmin.bat" publish exchange=amq.default routing_key=credit-responses payload="test_message"

# Просмотр сообщений
& "C:\Program Files\RabbitMQ Server\rabbitmq_server-3.12.0\sbin\rabbitmqadmin.bat" get queue=credit-responses


## Проект полностью соответствует требованиям:

Разворачивается одной командой docker-compose up

Обрабатывает заявки менее чем за 1 секунду

Использует Kafka для отправки заявок и RabbitMQ для ответов

Сохраняет данные в PostgreSQL с миграциями

Предоставляет REST API для работы с заявками