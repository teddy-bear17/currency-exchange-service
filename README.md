# Currency Exchange Service

REST API веб-сервис для управления списком валют, обменными курсами и проведения конвертации с интегрированным веб-интерфейсом. Приложение реализовано на Java с использованием нативного Jakarta Servlet API и встроенной базы данных SQLite.

Контекстный путь приложения: `/currency-exchange-service`.

---

## Стек технологий

* **Язык разработки:** Java 17+
* **Спецификация API:** Jakarta Servlet API 6.0
* **Сервлет-контейнер:** Apache Tomcat 10+ (с поддержкой спецификаций Jakarta EE)
* **База данных:** SQLite 3
* **Пул соединений:** HikariCP
* **Сериализация данных:** Jackson Databind
* **Сборка проекта:** Apache Maven
* **Контейнеризация:** Docker
* **Фронтенд:** HTML5, CSS3 (Bootstrap), JavaScript (jQuery)

---

## Архитектура проекта

```text
currency-exchange-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── DAO/                     # Доступ к данным через JDBC и PreparedStatement
│   │   │   ├── DTO/                     # Объекты передачи данных (DTO)
│   │   │   ├── exceptions/              # Кастомные исключения (NotFound, BadRequest, AlreadyExists)
│   │   │   ├── filters/                 # Обработка заголовков и кодировок (JsonFilter)
│   │   │   ├── models/                  # Доменные сущности (Currency, Exchange)
│   │   │   ├── servicies/               # Бизнес-логика, расчет курсов
│   │   │   ├── servlets/                # Обработка HTTP-запросов (GET, POST, PATCH)
│   │   │   ├── utils/                   # Инициализация пула соединений HikariCP
│   │   │   └── validators/              # Валидация входных кодов и параметров
│   │   ├── resources/
│   │   │   ├── currency.db              # Встроенная база данных SQLite
│   │   │   └── schema.sql               # DDL-скрипт инициализации таблиц
│   │   └── webapp/
│   │       ├── css/                     # Стили веб-интерфейса (Bootstrap)
│   │       ├── js/                      # Логика клиента (app.js, jQuery)
│   │       ├── WEB-INF/web.xml          # Конфигурация дескриптора развертывания
│   │       └── index.html               # Веб-страница приложения
├── Dockerfile                           # Конфигурация сборки Docker-образа
├── pom.xml                              # Конфигурация сборки Maven
└── .gitignore                           # Список исключений для Git
```

---

## Запуск в Docker-контейнере

Приложение собирается в `.war` архив и развертывается в контейнере Tomcat 10 с контекстным путем `/currency-exchange-service`.

### 1. Сборка WAR-артефакта
Соберите архив проекта с помощью Maven:
```bash
mvn clean package
```
В каталоге `target/` будет сгенерирован файл `currency-exchange-service.war`.

### 2. Сборка Docker-образа
Находясь в корневом каталоге проекта (рядом с `Dockerfile`), соберите образ:
```bash
docker build -t currency-exchange-service .
```

### 3. Запуск контейнера
Запустите контейнер с пробросом порта `8080`:
```bash
docker run -d -p 8080:8080 --name currency-exchange-service-container currency-exchange-service
```

После старта веб-интерфейс и REST API будут доступны по адресу:
```text
http://localhost:8080/currency-exchange-service/
```

### Команды для управления контейнером:
* Просмотр логов контейнера:
  ```bash
  docker logs -f currency-exchange-service-container
  ```
* Остановка контейнера:
  ```bash
  docker stop currency-exchange-service-container
  ```
* Удаление контейнера:
  ```bash
  docker rm currency-exchange-service-container
  ```

---

## Деплой на удаленный сервер Linux

Развертывание без использования Docker:
1. Соберите артефакт командой `mvn clean package`.
2. Подключитесь к VPS под управлением Ubuntu/Debian и установите JRE:
   ```bash
   sudo apt update && sudo apt install openjdk-17-jre-headless -y
   ```
3. Скачайте и запустите Apache Tomcat 10+.
4. Скопируйте файл `target/currency-exchange-service.war` в каталог `/opt/tomcat/webapps/` на сервере (или загрузите его через графический интерфейс Tomcat Manager).
5. Приложение развернется по адресу:
   ```text
   http://YOUR_SERVER_IP:8080/currency-exchange-service/
   ```

---

## Спецификация REST API

Все эндпоинты возвращают ответы в формате `application/json; charset=UTF-8`.  
Базовый префикс всех путей: `/currency-exchange-service`.

### 1. Валюты

* **`GET /currency-exchange-service/currencies`** — получение списка всех валют.  
  *Ответ (`200 OK`):*
  ```json
  [
    {
      "id": 1,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    }
  ]
  ```

* **`GET /currency-exchange-service/currency/{code}`** — получение конкретной валюты по коду (например, `/currency-exchange-service/currency/USD`)[cite: 2].  
  *Ответы:* `200 OK`, `400 Bad Request` (некорректный код), `404 Not Found` (валюта не найдена).

* **`POST /currency-exchange-service/currencies`** — добавление новой валюты.  
  *Формат:* `application/x-www-form-urlencoded` (`name`, `code`, `sign`).  
  *Ответы:* `201 Created`, `400 Bad Request`, `409 Conflict` (валюта уже существует).

---

### 2. Обменные курсы

* **`GET /currency-exchange-service/exchangeRates`** — получение всех курсов.  
  *Ответ (`200 OK`):*
  ```json
  [
    {
      "id": 1,
      "baseCurrency": { "id": 1, "name": "United States dollar", "code": "USD", "sign": "$" },
      "targetCurrency": { "id": 2, "name": "Euro", "code": "EUR", "sign": "€" },
      "rate": 0.95
    }
  ]
  ```

* **`GET /currency-exchange-service/exchangeRate/{baseCode}{targetCode}`** — получение курса пары (например, `/currency-exchange-service/exchangeRate/USDEUR`)[cite: 3].  
  *Ответы:* `200 OK`, `400 Bad Request`, `404 Not Found`.

* **`POST /currency-exchange-service/exchangeRates`** — добавление нового курса.  
  *Формат:* `application/x-www-form-urlencoded` (`baseCurrencyCode`, `targetCurrencyCode`, `rate`).  
  *Ответы:* `201 Created`, `400 Bad Request`, `404 Not Found`, `409 Conflict`.

* **`PATCH /currency-exchange-service/exchangeRate/{baseCode}{targetCode}`** — обновление курса валюты.  
  *Формат:* `application/x-www-form-urlencoded` (`rate`).  
  *Ответы:* `200 OK`, `400 Bad Request`, `404 Not Found`.

---

### 3. Конвертация валют

* **`GET /currency-exchange-service/exchange?from={base}&to={target}&amount={value}`** — расчет обмена суммы.  
  *Алгоритм поиска курса:*
    1. Прямой поиск курса ($A \rightarrow B$).
    2. Обратный поиск ($B \rightarrow A$) с расчетом: $rate = 1 / rate_{reverse}$.
    3. Кросс-курс через USD ($USD \rightarrow A$ и $USD \rightarrow B$) с расчетом: $rate = rate_{USD \rightarrow B} / rate_{USD \rightarrow A}$.

  *Ответ (`200 OK`):*
  ```json
  {
    "baseCurrency": { "id": 1, "name": "United States dollar", "code": "USD", "sign": "$" },
    "targetCurrency": { "id": 2, "name": "Euro", "code": "EUR", "sign": "€" },
    "rate": 0.95,
    "amount": 100.0,
    "convertedAmount": 95.00
  }
  ```
