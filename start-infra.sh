#powershell
# Запуск PostgreSQL (если установлен как сервис)
Start-Service postgresql-x64-13

# Запуск Zookeeper (предполагая, что Kafka установлена в C:\kafka)
Start-Process -NoNewWindow -FilePath "C:\kafka\bin\windows\zookeeper-server-start.bat" -ArgumentList "C:\kafka\config\zookeeper.properties"

# Запуск Kafka
Start-Process -NoNewWindow -FilePath "C:\kafka\bin\windows\kafka-server-start.bat" -ArgumentList "C:\kafka\config\server.properties"

# Запуск RabbitMQ (если установлен как сервис)
Start-Service RabbitMQ




#Linux
#
##!/bin/bash
#
## Запуск PostgreSQL (если не запущен)
#pg_ctl start -D /usr/local/var/postgres
#
## Запуск Zookeeper и Kafka
#/path/to/kafka/bin/zookeeper-server-start.sh -daemon /path/to/kafka/config/zookeeper.properties
#/path/to/kafka/bin/kafka-server-start.sh -daemon /path/to/kafka/config/server.properties
#
## Запуск RabbitMQ
#rabbitmq-server -detached