# Сервис BRT

## Описание

BRT-сервис (Billing Real-Time Service) — компонент телекоммуникационной биллинговой системы, отвечающий за управление абонентами, тарификацию услуг и обработку финансовых операций в режиме реального времени.

## Назначение

BRT-сервис выполняет следующие функции:
- Управление информацией об абонентах (создание, обновление данных, управление балансом)
- Обработка и фильтрация записей о звонках (CDR)
- Управление тарифными планами абонентов
- Обработка финансовых операций (пополнение баланса, списание средств по счетам)
- Отправка данных о звонках на тарификацию в HRS-сервис
- Интеграция с другими компонентами биллинговой системы

## Архитектурные особенности

BRT-сервис является центральным компонентом биллинговой системы, выступая в качестве основного хранилища данных об абонентах и обеспечивая интеграцию между CDR, HRS и CRM сервисами. Сервис использует событийно-ориентированную архитектуру с обменом сообщениями через RabbitMQ.

## Логика работы

### Управление абонентами

1.  **Создание абонента**:
    *   Метод `createSubscriber(SubscriberDTO subscriberDTO)` в `SubscriberService` принимает данные абонента.
    *   Проверяет уникальность MSISDN (номера телефона) через `findSubscriberByMsisdn`.
    *   Получает системное время от HRS-сервиса (`getSystemDatetime()`) для фиксации времени регистрации.
    *   Если указан `tariffId`, проверяет существование тарифа через HRS.
    *   Сохраняет абонента в базе данных с начальным балансом (по умолчанию 100 единиц).
    *   Отправляет уведомление о создании абонента через RabbitMQ.
    *   При необходимости привязывает тарифный план через HRS-сервис.

2.  **Назначение тарифа**:
    *   Метод `setTariffForSubscriber(Long subscriberId, Long tariffId)` проверяет существование абонента.
    *   Получает системное время от HRS-сервиса для синхронизации.
    *   Проверяет доступность тарифа через HRS.
    *   Обновляет информацию в локальной БД и оповещает HRS-сервис о смене тарифа.

3.  **Получение информации**:
    *   Метод `getSubscriberAndTariffInfo(Long subscriberId)` получает данные абонента из БД.
    *   Запрашивает информацию о текущем тарифе абонента от HRS-сервиса.
    *   Формирует объект `FullSubscriberAndTariffInfoDTO` с полной информацией.

4.  **Управление балансом**:
    *   Метод `addAmountToBalance`: Атомарно увеличивает баланс абонента с пессимистической блокировкой.
    *   Метод `subtractAmountFromBalance`: Атомарно списывает средства с баланса с пессимистической блокировкой.

### Обработка CDR

1. **Получение CDR**:
   * Метод `consumeCdr()` в `CdrConsumerService` получает записи CDR из очереди RabbitMQ.
   * Вызывает `processOurSubscribersCdr()` для фильтрации и обработки записей.

2. **Фильтрация записей**:
   * Метод `isOur()` определяет, является ли абонент клиентом нашей компании.
   * CDR абонентов других операторов игнорируются.

3. **Обработка записей CDR**:
   * Преобразует CDR в формат для тарификации (`CallWithDefaultMetadataDTO`).
   * Отправляет подготовленные данные в HRS-сервис через RabbitMQ.
   * Сохраняет исходные CDR-записи в базе данных для истории.

### Финансовые операции

1.  **Пополнение баланса**:
    *   API эндпоинт `PATCH /subscribers/{subscriberId}/balance` принимает `TopUpDTO`.
    *   Вызывает `addAmountToBalance()` для атомарного увеличения баланса.
    *   Возвращает сообщение об успешном пополнении.

2.  **Обработка счетов от HRS**:
    *   Метод `consumeBill(TarifficationBillDTO bill)` слушает очередь RabbitMQ для счетов.
    *   При получении счета вызывает `subtractAmountFromBalance()` для списания указанной суммы.

## Взаимодействие с другими сервисами

### HRS-сервис

BRT-сервис взаимодействует с HRS-сервисом через `HRSServiceClient` для следующих операций:
-   `setTariffForSubscriber`: Установка/изменение тарифа абонента.
-   `getSystemDatetime()`: Получение системного времени для синхронизации.
-   `getTariffInfoBySubscriberId`: Получение информации о текущем тарифе абонента.
-   `getTariffInfo`: Проверка существования тарифа по ID.

### RabbitMQ

Сервис использует следующие очереди и обменники:

1. **Получение CDR**:
   - Очередь: `${const.rabbitmq.cdr.CDR_QUEUE_NAME}`
   - Обработчик: `CdrConsumerService.consumeCdr()`

2. **Отправка данных для тарификации**:
   - Обменник: `${const.rabbitmq.tariffication.TARIFFICATION_EXCHANGE_NAME}`
   - Ключ маршрутизации: `${const.rabbitmq.tariffication.CALL_USAGE_ROUTING_KEY}`
   - Формат данных: `CallWithDefaultMetadataDTO`

3. **Получение счетов**:
   - Очередь: `${const.rabbitmq.bills.BILLS_QUEUE_NAME}`
   - Обработчик: `BillsConsumerService.consumeBill()`
   - Формат данных: `TarifficationBillDTO`

4. **Уведомления о новых абонентах**:
   - Обменник: `${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_EXCHANGE_NAME}`
   - Ключ маршрутизации: `${const.rabbitmq.subscriber.SUBSCRIBER_CREATED_ROUTING_KEY}`
   - Формат данных: `Map<String, Object>` с полями `subscriberId` и `msisdn`

## API-эндпоинты

### Управление абонентами

- `POST /subscriber` - Создание нового абонента
  - Входные данные: `SubscriberDTO`
  - Выходные данные: `Subscriber`

- `PUT /subscribers/{subscriberId}/tariff/{tariffId}` - Назначение тарифа
  - Входные данные: ID абонента, ID тарифа
  - Выходные данные: Сообщение об успехе

- `GET /subscribers/{subscriberId}` - Получение информации об абоненте
  - Выходные данные: `FullSubscriberAndTariffInfoDTO`

### Управление балансом

- `PATCH /subscribers/{subscriberId}/balance` - Пополнение баланса
  - Входные данные: `TopUpDTO` (содержит `amount` - сумму пополнения)
  - Выходные данные: Сообщение об успехе

## Обработка ошибок и Dead Letter Queues (DLQ)

Сервис реализует механизм Dead Letter Queue для обработки проблемных сообщений:

1. **Структура DLQ**:
   - Для каждой основной очереди создается DLQ с постфиксом `.dlq`
   - Создается Dead Letter Exchange с постфиксом `.dlx`
   - Используется специальный ключ маршрутизации с постфиксом `.dlr`

2. **Обработка ошибок**:
   - При ошибках обработки сообщения перенаправляются в соответствующую DLQ
   - Обработчик `rabbitExceptionsHandler` перехватывает и логирует ошибки
   - Сообщения в DLQ могут анализироваться вручную или автоматически

## Обработка ошибок

Сервис обрабатывает следующие исключения:
- `NoSuchSubscriberException`: Абонент не найден
- `SubscriberAlreadyExistsException`: Абонент с таким номером уже существует
- `SubscriberCreationFailedException`: Ошибка при создании абонента

Ошибки при взаимодействии с другими сервисами обрабатываются с сохранением исходных статусов.

## Конфигурационные параметры

- `const.hrs-service.BASE_URL`: URL для взаимодействия с HRS-сервисом
- `const.rabbitmq.cdr.CDR_QUEUE_NAME`: Очередь для получения CDR
- `const.rabbitmq.tariffication.TARIFFICATION_EXCHANGE_NAME`: Обменник для тарификации
- `const.rabbitmq.tariffication.CALL_USAGE_ROUTING_KEY`: Ключ маршрутизации для тарификации
- `const.rabbitmq.bills.BILLS_QUEUE_NAME`: Очередь для получения счетов
- `const.rabbitmq.subscriber.SUBSCRIBER_CREATED_EXCHANGE_NAME`: Обменник для новых абонентов
- `const.rabbitmq.subscriber.SUBSCRIBER_CREATED_ROUTING_KEY`: Ключ маршрутизации для новых абонентов

Параметры для Dead Letter Queues:
- `const.rabbitmq.dead-letter.DEAD_LETTER_EXCHANGE_POSTFIX`
- `const.rabbitmq.dead-letter.DEAD_LETTER_ROUTING_KEY_POSTFIX`
- `const.rabbitmq.dead-letter.DEAD_LETTER_QUEUE_POSTFIX`

## Технические детали

### Технологический стек

- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Cloud 2024.0.1**:
  - Eureka Client для обнаружения сервисов
  - Config Client для централизованной конфигурации
- **Spring Data JPA** для работы с базой данных
- **Spring AMQP** для работы с RabbitMQ
- **RestClient** для взаимодействия с другими сервисами
- **PostgreSQL** в качестве СУБД
- **Liquibase** для управления схемой БД
- **JUnit 5** и **Mockito** для тестирования

### База данных

**PostgreSQL**
- JDBC URL: `jdbc:postgresql://localhost:5432/brt-db` (локальная разработка)
- JDBC URL: `jdbc:postgresql://brt-db:5432/brt-db` (контейнеры)
- Пользователь: `postgres`
- Пароль: `postgres`
- Схема: `public`

### Мониторинг

Сервис включает компоненты мониторинга:
- Health-чеки для проверки работоспособности
- Интеграция с Actuator для экспорта метрик и состояния
- Логирование ключевых операций через SLF4J

