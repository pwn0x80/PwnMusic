# 🎶 Telegram SongBot

A **Spring Boot-based Telegram music bot** that integrates **Apache Kafka, Spring Data JPA, H2 Database, and the Telegram Bot API** to process song and playlist requests asynchronously and deliver them directly to Telegram chats.

## 🚀 Features

* **Telegram Bot Integration** — Interact with Telegram chats, groups, and channels through the Telegram Bot API.
* **Asynchronous Event Processing** — Uses Apache Kafka for decoupled, event-driven processing of song requests and events.
* **Song & Playlist Processing** — Supports processing individual songs and playlists for delivery to Telegram.
* **Database Persistence** — Stores song metadata using Spring Data JPA and H2.
* **Environment-Based Configuration** — Keeps sensitive credentials such as bot tokens and chat IDs outside the source code.
* **Testable Architecture** — Includes Spring Boot tests for validating application components and integrations.

## 🛠️ Tech Stack

| Technology           | Purpose                         |
| -------------------- | ------------------------------- |
| **Java 17+**         | Application development         |
| **Spring Boot**      | Backend framework               |
| **Spring Data JPA**  | Database persistence            |
| **Apache Kafka**     | Asynchronous messaging          |
| **H2 Database**      | Local development database      |
| **Telegram Bot API** | Telegram integration            |
| **Maven**            | Build and dependency management |

## 🏗️ Architecture

```text
                 ┌─────────────────────┐
                 │      Telegram       │
                 │   User / Channel    │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    Spring Boot      │
                 │     Telegram Bot    │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │   Kafka Producer    │
                 │  Song/Playlist Event│
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    Apache Kafka     │
                 │       Topic         │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │   Kafka Consumer    │
                 │  Processing Logic   │
                 └──────────┬──────────┘
                            │
                 ┌──────────┴──────────┐
                 ▼                     ▼
        ┌─────────────────┐   ┌─────────────────┐
        │   H2 Database   │   │    Telegram     │
        │ Song Metadata   │   │ Message/Media   │
        └─────────────────┘   └─────────────────┘
```

## 🔐 Configuration & Secrets

Sensitive configuration is loaded through environment variables rather than being committed to the repository.

Create a `.env` file in the project root:

```env
TELEGRAM_BOT_TOKEN=your_telegram_bot_token_here
TELEGRAM_BOT_CHAT_ID=your_chat_id_here
TELEGRAM_BOT_MESSAGE_THREAD_ID=your_thread_id_here

SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

SPRING_DATASOURCE_URL=jdbc:h2:mem:songbot_db;DB_CLOSE_DELAY=-1
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
```

> **Important:** Never commit your actual Telegram bot token or other credentials to GitHub.

The `.env` file is included in `.gitignore` and should remain local.

## 📋 Prerequisites

Make sure the following are installed:

* **Java 17 or higher**
* **Maven**
* **Apache Kafka**
* **Git**

Kafka should be running locally on:

```text
localhost:9092
```

## 🏃 Running the Application

### 1. Clone the repository

```bash
git clone <repository-url>
cd <project-directory>
```

### 2. Configure environment variables

Create the `.env` file in the project root and add your Telegram and database configuration.

### 3. Start Kafka

Make sure your Kafka broker is running and available at:

```text
localhost:9092
```

### 4. Start the Spring Boot application

Using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

## 🧪 Running Tests

Run the complete test suite with:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

## 🗄️ Database

The application uses **H2** for local development and persistence.

Current configuration:

```text
jdbc:h2:mem:songbot_db;DB_CLOSE_DELAY=-1
```

The database is configured as an in-memory database and remains available while the application is running.

## 🔄 Event Flow

A typical song request follows this flow:

```text
Telegram Request
       ↓
Spring Boot Bot
       ↓
Kafka Producer
       ↓
Kafka Topic
       ↓
Kafka Consumer
       ↓
Song Processing
       ↓
Save Metadata → H2
       ↓
Send Result → Telegram
```

This event-driven approach allows song processing to be decoupled from the Telegram interaction layer and provides a foundation for scaling individual processing components independently.

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── ...
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── ...
```

## 🔒 Security

The project follows basic secret-management practices:

* Telegram bot credentials are stored outside the source code.
* `.env` is excluded through `.gitignore`.
* Credentials should never be hardcoded or committed to Git.
* Production deployments should use a proper secret-management solution such as environment secrets, Docker/Kubernetes secrets, or a cloud secret manager.

## 🚧 Future Improvements

Potential improvements include:

* PostgreSQL/MySQL support for production deployments
* Redis caching
* Kafka retry and dead-letter topics
* Docker and Docker Compose deployment
* Persistent song/file storage using object storage
* Download progress tracking
* Multiple concurrent song-processing workers
* Monitoring with Spring Boot Actuator
* Metrics and observability
* Authentication and authorization for bot administration

## 📄 License

This project is intended for learning and personal development purposes.
