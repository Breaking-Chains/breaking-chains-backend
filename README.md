# Breaking Chains - Java Spring Boot Backend Service

Java 17 & Spring Boot 3 REST API service for **Breaking Chains** Android application. Adheres strictly to [`API_CONTRACT.md`](../breaking-chains-backend/API_CONTRACT.md).

---

## Technical Stack

- **Runtime & Language:** Java 17, Spring Boot 3.3.x
- **Build Tool:** Gradle
- **Database & ORM:** PostgreSQL + Spring Data JPA (Hibernate)
- **Dev Tool:** Spring Boot DevTools (Hot Reloading / Live Reload)
- **Authentication:** JWT (Access + Refresh token rotation via JJWT), Spring Security, Google Identity (`google-api-client`)
- **Validation:** Spring Boot Starter Validation (`jakarta.validation`)

---

## Quick Start (Option 1: Database in Docker + App Native) 🚀

This is the recommended local development workflow for maximum speed and instant live reloads.

### 1. Start PostgreSQL Database in Docker
From the project directory, start only the database container in detached mode:

```bash
docker compose up postgres -d
```

*The database will run on `localhost:5433` with database `breaking_chains_db`, user `postgres`, password `password`.*

### 2. Run Spring Boot Server
In PowerShell / Terminal, launch the development server:

**Windows:**
```powershell
.\gradlew bootRun
```

**macOS / Linux:**
```bash
./gradlew bootRun
```

The application will start on **`http://localhost:8080`**. Test the health check endpoint:
```http
GET http://localhost:8080/health
```

---

## Hot Reloading with Spring Boot DevTools 🔥

Spring Boot DevTools is included in `build.gradle`:
```groovy
developmentOnly 'org.springframework.boot:spring-boot-devtools'
```

- When running `.\gradlew bootRun`, any changes to `.class` files or `application.yml` automatically trigger an **instant application context restart (~1s)**.
- **IDE Tip:** Enable "Build Automatically" (or press `Ctrl + F9` / `Ctrl + S` depending on your IDE) so compiled `.class` files update automatically when you save.

---

## Alternative: Run Entire Stack in Docker 🐳

To run both PostgreSQL and Spring Boot inside Docker containers (e.g. for teammates without Java installed locally):

```bash
docker compose up --build
```

---

## API Endpoints Overview

| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/health` | No | Health check endpoint |
| `POST` | `/api/v1/auth/register` | No | Register new user with email & password |
| `POST` | `/api/v1/auth/login` | No | Login with email & password |
| `POST` | `/api/v1/auth/google` | No | Authenticate via Google ID Token |
| `POST` | `/api/v1/auth/refresh` | No | Obtain new access token via refresh token |
| `POST` | `/api/v1/auth/logout` | Yes | Revoke refresh token |
| `GET` | `/api/v1/users/me` | Yes | Get logged-in user profile |
| `PUT` | `/api/v1/users/me` | Yes | Update logged-in user profile |

---

## Building Executable Production JAR

To build a standalone production executable JAR:

```bash
# Windows
.\gradlew bootJar

# macOS / Linux
./gradlew bootJar
```

The output JAR will be placed at: `build/libs/breaking-chains-backend-1.0.0.jar`.
