# Todo List — Система управления задачами

## Описание проекта

REST API для управления задачами с поддержкой подзадач и статусов. Система позволяет создавать задачи с приоритетами и дедлайнами, разбивать их на подзадачи и отслеживать прогресс через настраиваемые статусы.

## Архитектура

### Компоненты

- **Spring Boot** — REST API приложение для управления задачами (Java 21)
- **PostgreSQL 15** — реляционная база данных для хранения информации
- **Docker Network (bridge)** — изолированная сеть для взаимодействия контейнеров

### Структура данных

Реляционная база содержит таблицы:

- `statuses` — статусы задач (id, name)
- `tasks` — задачи (id, title, description, status_id, priority, due_date, created_at, updated_at)
- `subtasks` — подзадачи (id, task_id, title, is_completed, position, created_at)

## Быстрый старт

### Требования

- Docker и Docker Compose
- Файл `.env` с переменными окружения

### Установка

1. **Подготовить `.env` файл** в корне проекта:

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=todolist
```

2. **Запустить сервисы**:

```bash
docker compose down -v
docker compose build --no-cache
docker compose up
```

3. **Проверить логи**:

```bash
docker compose logs -f app
```

Приложение будет доступно по адресу `http://localhost:8080`.

## API Endpoints

### Статусы

```bash
# Получить все статусы
GET /api/statuses

# Создать статус
POST /api/statuses
Content-Type: application/json
{
  "name": "In Progress"
}

# Обновить статус
PUT /api/statuses/{id}
Content-Type: application/json
{
  "name": "Done"
}

# Удалить статус
DELETE /api/statuses/{id}
```

### Задачи

Поле `priority` принимает одно из трёх значений: `"LOW"`, `"MEDIUM"`, `"HIGH"`.

```bash
# Получить все задачи
GET /api/tasks

# Получить задачу по ID
GET /api/tasks/{id}

# Создать задачу
POST /api/tasks
Content-Type: application/json
{
  "title": "Написать документацию",
  "description": "Оформить README и описание API",
  "statusId": 1,
  "priority": "HIGH",
  "dueDate": "2026-04-01"
}

# Обновить задачу
PUT /api/tasks/{id}
Content-Type: application/json
{
  "title": "Написать документацию",
  "description": "Оформить README и описание API",
  "statusId": 2,
  "priority": "MEDIUM",
  "dueDate": "2026-04-01"
}

# Удалить задачу
DELETE /api/tasks/{id}
```

### Подзадачи

```bash
# Получить подзадачи по ID задачи
GET /api/subtasks/task/{taskId}

# Создать подзадачу
POST /api/subtasks
Content-Type: application/json
{
  "taskId": 1,
  "title": "Что-то",
  "position": 1
}

# Переключить статус выполнения подзадачи
PATCH /api/subtasks/{id}/toggle

# Удалить подзадачу
DELETE /api/subtasks/{id}
```

## Реализованные функции

### Основной функционал

- [x] Двухконтейнерная архитектура (Spring Boot App + PostgreSQL)
- [x] Bridge-сеть для изолированного взаимодействия контейнеров
- [x] REST API с CRUD операциями для всех сущностей
- [x] Управление статусами (создание, просмотр, редактирование, удаление)
- [x] Управление задачами (создание, просмотр, редактирование, удаление)
- [x] Управление подзадачами (создание, просмотр, переключение выполнения, удаление)

### Требования к Docker (соблюдены)

- [x] Организована docker-сеть в Docker-Compose файле (bridge network `todolist-network`)
- [x] Билд Docker-контейнера выполняется на любом окружении (multi-stage build с Maven)
- [x] Dockerfile разбит на разные stages (build и runtime)
- [x] Доступ извне docker-сети только к API (порт 8080 открыт только для app)
- [x] База данных без port-forwarding (скрыта в docker-сети)
- [x] Volume для PostgreSQL (`postgres_data`)
- [x] Отсутствуют пароли в Dockerfile и docker-compose
- [x] Приложение получает конфиденциальные данные из переменных окружения

## Технические детали

### Multi-stage Dockerfile

Dockerfile использует два этапа для оптимизации размера образа:

1. **Build stage** — использует Maven 3.9 с JDK 21 для сборки приложения
    - Копирует `pom.xml` и загружает зависимости
    - Компилирует проект и создаёт JAR-файл
2. **Runtime stage** — использует облегчённый `eclipse-temurin:21-jre`
    - Содержит только необходимые компоненты для запуска
    - Копирует готовый JAR из build stage

Результат: образ содержит только JRE и приложение, без инструментов сборки.

### Docker Compose конфигурация

- **PostgreSQL** — работает в контейнере с изолированным хранилищем (volume `postgres_data`), инициализируется скриптом `init.sql`
- **Spring Boot App** — зависит от БД (`depends_on`), получает параметры подключения из переменных окружения
- **Bridge Network** — контейнеры взаимодействуют по имени сервиса (`db:5432`)

### Переменные окружения

Приложение использует следующие переменные:

- `POSTGRES_USER` — имя пользователя PostgreSQL
- `POSTGRES_PASSWORD` — пароль PostgreSQL
- `POSTGRES_DB` — название базы данных
- `SPRING_DATASOURCE_URL` — URL подключения к БД (конструируется автоматически из переменных)
- `SPRING_DATASOURCE_USERNAME` — логин для приложения
- `SPRING_DATASOURCE_PASSWORD` — пароль для приложения