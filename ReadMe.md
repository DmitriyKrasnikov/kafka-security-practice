# Инструкция по проверке проекта

В данном проекте реализованы три варианта работы с Kafka:

1. **Через консоль внутри Docker-контейнера**: создание топиков, настройка ACL и отправка/чтение сообщений с помощью консольных утилит Kafka.
2. **Простой Java-вариант**: продюсер и консьюмер, написанные на нативном Kafka-клиенте без Spring Boot.
3. **Spring Boot вариант**: продюсер и консьюмер на базе Spring Boot с конфигурацией через аннотации и классы конфигурации.

## 1. Создание топиков

Выполните команды, чтобы создать два топика (`topic-1` и `topic-2`) с одной партицией и фактором репликации 3:

```bash
# Создать topic-1
kafka-topics \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --create \
  --topic topic-1 \
  --partitions 1 \
  --replication-factor 3

# Создать topic-2
kafka-topics \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --create \
  --topic topic-2 \
  --partitions 1 \
  --replication-factor 3
```

## 2. Настройка ACL для `topic-1`

Выдайте всем принципалам права на **запись (WRITE)** и **чтение (READ)** для `topic-1`:

```bash
# Разрешить WRITE всем
kafka-acls \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --add \
  --allow-principal User:* \
  --operation Write \
  --topic topic-1

# Разрешить READ всем
kafka-acls \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --add \
  --allow-principal User:* \
  --operation Read \
  --topic topic-1
```

## 3. Настройка ACL для `topic-2`

Выдайте всем принципалам право на **запись (WRITE)** и **запретите чтение (READ)** для `topic-2`:

```bash
# Разрешить WRITE всем
kafka-acls \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --add \
  --allow-principal User:* \
  --operation Write \
  --topic topic-2

# Запретить READ всем
kafka-acls \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --add \
  --deny-principal User:* \
  --operation Read \
  --topic topic-2
```

## 4. Проверка

### Список топиков

```bash
kafka-topics \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --list
```

### Просмотр ACL для каждого топика

```bash
# ACL для topic-1
kafka-acls \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --list \
  --topic topic-1

# ACL для topic-2
kafka-acls \
  --bootstrap-server kafka-1:9093 \
  --command-config /etc/kafka/client.properties \
  --list \
  --topic topic-2
```

## 5. Функциональные тесты

> 🔄 Функциональные тесты можно также провести запуском Java-классов продюсера и консьюмера (как в простом варианте, так и через Spring Boot).

С помощью консольного продюсера и консьюмера проверьте применение ACL:

```bash
# -------------------------
# Producer → topic-1 (должен пройти)
# -------------------------
kafka-console-producer \
  --bootstrap-server kafka-1:9093 \
  --producer.config /etc/kafka/user.properties \
  --topic topic-1 <<EOF
hello topic-1
EOF

# -------------------------
# Producer → topic-2 (должен пройти)
# -------------------------
kafka-console-producer \
  --bootstrap-server kafka-1:9093 \
  --producer.config /etc/kafka/user.properties \
  --topic topic-2 <<EOF
hello topic-2
EOF

# -------------------------
# Consumer ← topic-1 (должен прочитать)
# -------------------------
kafka-console-consumer \
  --bootstrap-server kafka-1:9093 \
  --consumer.config /etc/kafka/user.properties \
  --topic topic-1 \
  --from-beginning \
  --timeout-ms 10000

# -------------------------
# Consumer ← topic-2 (должен НЕ прочитать) получите org.apache.kafka.common.errors.TopicAuthorizationException: Not authorized to access topics: [topic-2]
# -------------------------
kafka-console-consumer \
  --bootstrap-server kafka-1:9093 \
  --consumer.config /etc/kafka/user.properties \
  --topic topic-2 \
  --from-beginning \
  --timeout-ms 10000
```

## 6. Проверка через запуск Java-классов

Запустите Java-классы продюсера и консьюмера (в простом варианте и через Spring Boot). В логах приложения вы увидите подобные логи:

* **Отправка сообщений** (INFO):

    * `Sent message to topic-1: key=...`,
    * `Sent message to topic-2: key=...`
* **Чтение из topic-1** (INFO):

    * `Received message from topic-1: ...`
* **Ошибка при чтении из topic-2** (ERROR/WARN):

    * `org.apache.kafka.common.errors.TopicAuthorizationException: Not authorized to access topics: [topic-2]`

Просмотрите логи в консоли, чтобы убедиться, что сообщения и ошибки отображаются корректно.
