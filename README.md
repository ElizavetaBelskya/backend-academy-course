# Simple java app

Простая CRUD база данных магазина, консольная программа для хранения и управления информацией о товарах в магазине. Данные хранятся в памяти, а управление осуществляется через текстовые команды.
Программа поддерживает базовые операции CRUD.

### 📌 Функциональность
- **Добавление товара** с уникальным артикулом
- **Вывод списка товаров** в виде таблицы
- **Обновление данных товара** по артикулу
- **Удаление товара** по артикулу
- **Завершение работы программы** по команде `exit`

### 🔧 Использование

Программа принимает следующие команды:

| Команда | Описание |
|---------|----------|
| `create $артикул $название $цена $количество` | Добавить новый товар в базу |
| `read` | Вывести список всех товаров |
| `update $артикул $название $цена $количество` | Обновить информацию о товаре |
| `delete $артикул` | Удалить товар по артикулу |
| `exit` | Завершить работу программы |

### ⚠️ Валидация данных
- **Артикул** должен быть **уникальным** и содержать **только латинские буквы в верхнем регистре и цифры**.
- **Название** можно вводить в кавычках, если оно состоит из нескольких слов.
- **Цена** и **количество** должны быть числами.

# Simple Spring app

Простая Web CRUD база данных магазина

### 📌 Описание

Это веб-приложение, реализующее CRUD-операции (Create, Read, Update, Delete) для управления товарами в магазине через HTTP API.

Приложение разработано с использованием Spring Framework и поддерживает два варианта хранения данных:
- В оперативной памяти (ram)
- В файле (file)

### 📌 API 

| Метод   | URL                  | Описание                      |
|---------|----------------------|--------------------------------|
| `POST`  | `/product`           | Добавляет новый товар         |
| `GET`   | `/product`           | Возвращает список всех товаров |
| `PUT`   | `/product/{article}` | Обновляет товар по артикулу   |
| `DELETE`| `/product/{article}` | Удаляет товар по артикулу     |


### ⚠️ Валидация данных

- Артикул должен быть уникальным и содержать только латинские буквы в верхнем регистре и цифры.
- Название может содержать пробелы.
- Цена и количество должны быть положительными числами.


# Category app

Расширено приложение из предыдущего ДЗ.
Добавлена поддержка категорий: товары теперь привязываются к категориям,
у категорий есть уникальный ID и URL на латинице. Если категория не указана, товар попадает в дефолтную. 
Добавлены API-эндпоинты для управления категориями.

### 🏷 API

| Метод   | URL                      | Описание                                        |
|---------|--------------------------|------------------------------------------------|
| `POST`  | `/category`               | Добавляет новую категорию                      |
| `GET`   | `/category`               | Возвращает список всех категорий с товарами   |
| `GET`   | `/category/{categoryId}`  | Возвращает категорию и список её товаров      |
| `PUT`   | `/category/{categoryId}`  | Обновляет информацию о категории по ID        |
| `DELETE`| `/category/{categoryId}`  | Удаляет категорию по ID                        |

### ⚠️ Валидация данных
- Название категории должно автоматически формировать уникальный URL латиницей.
- Товары без указанной категории добавляются в дефолтную категорию.
- Если категория не найдена, возвращается ошибка.
- ID категорий должны быть уникальными даже при одновременных запросах на создание, без блокировок.
- В запросах на создание или обновление товара добавлено поле category, указывающее, к какой категории принадлежит товар. Если категория не указана, товар попадает в дефолтную категорию.

# Async category app

### 📌 Описание

В интернет-магазине из предыдущих заданий появилось множество товаров и категорий.  
Для удобства управления теперь необходимо реализовать **асинхронный перенос товаров** между категориями.
Задача переноса выполняется **в фоне**, а её статус можно отслеживать.

### 🚀 API эндпоинты

**`POST /move-products-task`**  
Создаёт задачу для переноса товаров из одной категории в другую. Метод выполняется **асинхронно**.

- **Тело запроса (JSON):**

  ```json
  {
      "sourceCategoryId": 1,
      "targetCategoryId": 2
  }


POST /move-products-task

Входные данные:
```
{
    "sourceCategoryId": 1
    "targetCategoryId": 2
}
```
Результат запроса:
```
{
    "taskId": 432
}
```
### Получение статуса запущенной задачи
GET /task-status?taskId=432

Результат запроса:
```
{
    "taskId": 432
    "taskStatus": WAITING / IN_PROGRESS / DONE / ERROR
}
```


# Queue app

Отчет о реализации системы обработки задач.

### Функциональность

### 1. Очередь на обработку задач

- Человек подходит к терминалу, который отправляет `POST /task`, получая номер в очереди.
- Оператор получает следующую задачу в статусе `WAITING`, вызывая `GET /task`.
- Оператор обновляет статус задачи по номеру `PATCH /task/number`.
- Оператор может удалить задачу `DELETE /task/number`.
- Экран отображает задачи по статусам (кроме `CLOSE`), используя `GET /tasks`.

### 2. Объекты

#### Task

```java
class Task {
    String number;  // Уникальный идентификатор (цифры и буквы)
    TaskStatus status; 
    List<TaskStatusTime> times; // История времени по статусам
}
```

#### TaskStatus

```java
class TaskStatus {
    Map<TaskStatus, List<String>> statusMap; // Список задач по статусам
}
```

#### TaskStatusTimeMap

```java
class TaskStatusTimeMap {
    List<TaskStatusTime> times; // Время обработки по статусам
}
```

### 3. Логика смены статусов

- **NEW → WAITING → PROCESSED → CLOSE**
- Задача может перейти в статус `CANCEL` из любого состояния.
- Разрешен переход на шаг вперед или назад.
- Если задача из `PROCESSED` переводится в `WAITING`, она ставится в конец очереди.

### 4. Очередь

- Используется `ConcurrentLinkedQueue` для обработки очереди.

### API для менеджера

### 1. Среднее время обработки задач

- `GET /times` → возвращает `TaskStatusTimeMap`

### 2. Время обработки конкретной задачи

- `GET /times/{number}` → возвращает `Double` (время обработки задачи)

### 3. Время обработки по статусу задачи

- `GET /times/{number}/{status}` → возвращает `Double` (время обработки задачи в конкретном статусе)

### 4. Среднее время обработки по статусам

- `GET /times/status` → возвращает `TaskStatusTimeMap`

### Дополнительные требования

- **Использование:** `MapStruct`, `Swagger`, `Lombok`
- **Покрытие тестами:** >90%
- \*\*Автоматический переход в \*\*\`\` для задач, находящихся в статусе, отличном от `NEW`, дольше 30 минут (проверка каждую минуту по `cron`).

### Реализация в проекте

### 1. Файл `service/TaskStatusChecker.java`

- Отвечает за обработку таймаутов и перевод задач в `CANCEL`.

### 2. Swagger-документация (`swagger.yml`)

- Описаны API-интерфейсы.
- В `resources/definitions` хранятся DTO.
- Используется `swagger-codegen` для генерации API и DTO.

### 3. Реализация SOAP-сервиса

- Контракт в `resources/xsd/soap.xsd`.
- Используется `jaxb2-maven-plugin` (`goal xjc`).
- Ошибки передаются через `Fault`.
- Аутентификация не настроена.

### 4. Реализация gRPC

- Контракт в `protobuf/tasks.proto`.
- Код генерируется через `protobuf-maven-plugin`.
- Аутентификация реализована через интерцептор.
- Используется `@GrpcAdvice` для обработки исключений.
- Исключения передаются как `StatusException`.
- Упрощенная архитектура (без дополнительного сервисного слоя).

### 5. Реализация безопасности (`SecurityFilterChain`)

- Доступ к `POST /task` и `GET /tasks` открыт (`permitAll`).
- Доступ к остальным эндпоинтам только для ролей `"manager"`, `"user"`.
- Пароли хранятся в `application.properties` или переменной окружения (`env`).
- Тестирование реализовано.

## Архитектура проекта

- Общие модули (`repository`, `exceptions`) вынесены в отдельные пакеты.
- Реализации для разных протоколов (`REST`, `SOAP`, `gRPC`) разнесены по разным пакетам.

---

## Итог

✅ Покрытие тестами выше 90%. 


# Course web app

### 1. Описание проекта

Создано веб-приложение с использованием **Java**, **Spring Boot** и базы данных для хранения информации о студентах и курсах.

- **Студент** (Student) связан с одним или несколькими **курсами** (Course).

### API
Реализованы следующие эндпоинты:
- Создание нового студента.
- Получение списка всех студентов.
- Получение информации о студенте по идентификатору.
- Добавление студента к курсам.
- Создание нового курса.
- Получение списка всех курсов.
- Получение информации о курсе по идентификатору.
- Редактирование данных студента и его курсов 

### Взаимодействие с базой данных
- Использованы **Spring Data JPA**, **Hibernate**, **JdbcTemplate**, **PostgreSQL**.
- Реализована защита от параллельного обновления с помощью optimistic-lock.

### Обработка исключений
- Добавлен механизм обработки ошибок с информативными сообщениями.

### Тестирование
- Написаны юнит-тесты для контроллеров и сервисов.


# Mongo Webflux app

Реактивное приложение, в качестве баз используется MongoDb, Redis для кэширования.

### Описание функционала приложения

| HTTP Метод | Эндпоинт | Описание | Ответ | Контроллер |
|------------|------------|------------|------------|------------|
| **GET** | `/topics/{idt}/messages/{idm}` | Получить сообщение по ID | `Mono<Topic>` | `MessageController` |
| **GET** | `/topics/{idt}/messages` | Получить все сообщения по ID топика | `Flux<Topic>` | `MessageController` |
| **POST** | `/topics/{idt}/messages` | Создать новое сообщение в топике | `Mono<Topic>` | `MessageController` |
| **PUT** | `/topics/{idt}/messages/{idm}` | Обновить сообщение по ID | `Mono<Topic>` | `MessageController` |
| **DELETE** | `/topics/{idt}/messages/{idm}` | Удалить сообщение по ID | `Mono<Void>` | `MessageController` |
| **POST** | `/topics/{idt}/messages/{idm}/comments` | Добавить комментарий к сообщению | `Mono<Topic>` | `MessageController` |
| **PUT** | `/topics/{idt}/messages/{idm}/comments` | Обновить комментарий в сообщении | `Mono<Topic>` | `MessageController` |
| **DELETE** | `/topics/{idt}/messages/{idm}/comments/{idc}` | Удалить комментарий из сообщения | `Mono<Void>` | `MessageController` |
| **GET** | `/topics/{id}` | Получить топик по ID | `Mono<Topic>` | `TopicController` |
| **POST** | `/topics` | Создать новый топик | `Mono<Topic>` | `TopicController` |
| **PUT** | `/topics/{id}` | Обновить топик по ID | `Mono<Topic>` | `TopicController` |
| **DELETE** | `/topics/{id}` | Удалить топик и все его сообщения | `Mono<Void>` | `TopicController` |

### Тестирование
Покрытие тестами около 90%, добавлена валидация покрытия тестов в .github actions.

# Kafka RPS research

Эксперимент с Kafka: Производительность передачи данных

### Описание эксперимента

Цель эксперимента – оценить производительность и надежность передачи данных через Apache Kafka при различных сценариях публикации и потребления данных.

### Структура данных

### Таблица `person`
```sql
CREATE TABLE person (
    id BIGINT,
    name VARCHAR,
    about_me TEXT,
    birthdate DATETIME
);
```

### Таблица `child`
```sql
CREATE TABLE child (
    parent_id BIGINT,
    child_id BIGINT
);
```

### Заполнение данных

1. Заполнить таблицу `person`:
  - 1 000 000 записей с `about_me` не более 10 символов (**Тип А**).
  - 1 000 000 записей с `about_me` более 1000 символов (**Тип Б**).
2. Заполнить таблицу `child`:
  - 500 000 людей из (А) имеют по **1 ребенку**.
  - 20 000 людей из (Б) имеют по **5 детей**.

### Архитектура Kafka

- Для каждого эксперимента создается отдельный **топик** в Kafka.
- В проект добавлены **паблишер** (Producer) и **консьюмер** (Consumer).
- Формат передаваемых данных – **JSON**, содержащий родителя и список его детей.

### Проверяемые сценарии

1. **Отправка без гарантии чтения**
2. **Отправка с гарантией чтения**
3. **Пакетное чтение без гарантии чтения**
4. **Пакетное чтение с гарантией чтения**
5. **Пакетное чтение с фильтром в листенере с гарантией чтения**
6. **Пакетное чтение с фильтром внутри консьюмера с гарантией чтения**
7. **Отправка данных через Avro**

### Анализ результатов

После выполнения эксперимента необходимо:

- Сформировать таблицу для **7 топиков**.
- Замерить **RPS (Requests Per Second)** отправки и чтения.
- Разделить данные по **Типам А и Б**.
- Сделать выводы о влиянии длины сообщения и способа чтения на производительность.