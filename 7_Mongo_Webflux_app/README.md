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