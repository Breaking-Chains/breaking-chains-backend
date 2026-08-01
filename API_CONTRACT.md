# Breaking Chains Backend - API Contract

**Framework:** Spring Boot 3 (Java 17)  
**Base URL (Local):** `http://localhost:8080`  
**Base URL (Production):** `https://breaking-chains-backend.onrender.com`  
**API Version:** `v1`  
**Root Path:** `/api/v1`

---

## 1. Global Response Envelopes

All API endpoints return JSON wrapped in a consistent structure.

### 1.1 Success Response (`ApiResponse<T>`)
```json
{
  "status": "success",
  "message": "Optional contextual message (present when applicable)",
  "data": { ... }
}
```

### 1.2 Error Response (`ApiErrorResponse`)
```json
{
  "status": "error",
  "code": "ERROR_CODE_STRING",
  "message": "Human readable error summary",
  "details": {
    "field": "Specific validation error or field issue detail"
  }
}
```

---

## 2. Standard Error Codes & HTTP Statuses

| HTTP Status | Error Code | Description |
| :--- | :--- | :--- |
| `400 Bad Request` | `VALIDATION_ERROR` | Request body failed Jakarta `@Valid` constraints or payload parameter rules. |
| `400 Bad Request` | `MALFORMED_JSON` | Required request body is missing or contains malformed JSON. |
| `400 Bad Request` | `INVALID_PARAMETER` | Path variable or parameter type mismatch (e.g. invalid UUID format). |
| `401 Unauthorized` | `INVALID_CREDENTIALS` | Incorrect email or password provided during login. |
| `401 Unauthorized` | `UNAUTHORIZED` | Missing, malformed, or expired JWT Access Token in `Authorization` header. |
| `401 Unauthorized` | `TOKEN_EXPIRED` | JWT Access Token has expired. |
| `401 Unauthorized` | `INVALID_REFRESH_TOKEN` | Refresh Token is invalid, expired, or corrupted. |
| `403 Forbidden` | `FORBIDDEN` | Authenticated user lacks permission to access target resource. |
| `404 Not Found` | `NOT_FOUND` | Requested user or resource does not exist. |
| `405 Method Not Allowed` | `METHOD_NOT_ALLOWED` | HTTP method not supported for target URI. |
| `409 Conflict` | `USER_EXISTS` | Email or username already registered in the system. |
| `500 Server Error` | `INTERNAL_SERVER_ERROR` | Unhandled internal server exception. |

---

## 3. Security & Authentication Header

For all endpoints requiring authentication, pass the JWT access token in the standard HTTP `Authorization` header:

```http
Authorization: Bearer <accessToken>
```

---

## 4. Endpoints Specification

### 4.1 System Health Check

Check application status and database readiness.

- **Method:** `GET`
- **Path:** `/health`
- **Authentication:** None
- **Response `200 OK`:**
  ```json
  {
    "status": "UP",
    "service": "breaking-chains-backend",
    "framework": "Spring Boot 3 (Java 17)"
  }
  ```

---

### 4.2 Authentication Endpoints (`/api/v1/auth`)

#### 4.2.1 Register User
Create a new user account with local credentials.

- **Method:** `POST`
- **Path:** `/api/v1/auth/register`
- **Authentication:** None
- **Request Body:**
  ```json
  {
    "email": "john.doe@example.com",
    "password": "Password123!",
    "fullName": "John Doe",
    "username": "johndoe"
  }
  ```

- **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "data": {
      "user": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "email": "john.doe@example.com",
        "fullName": "John Doe",
        "username": "johndoe",
        "avatarUrl": null,
        "bio": null,
        "authProvider": "LOCAL",
        "createdAt": "2026-08-01T07:15:00.000Z",
        "updatedAt": "2026-08-01T07:15:00.000Z"
      },
      "tokens": {
        "accessToken": "eyJhbGciOi...",
        "refreshToken": "eyJhbGciOi..."
      }
    }
  }
  ```

---

#### 4.2.2 Login User
Authenticate using email and password.

- **Method:** `POST`
- **Path:** `/api/v1/auth/login`
- **Authentication:** None
- **Request Body:**
  ```json
  {
    "email": "john.doe@example.com",
    "password": "Password123!"
  }
  ```

- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "user": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "email": "john.doe@example.com",
        "fullName": "John Doe",
        "username": "johndoe",
        "avatarUrl": null,
        "bio": null,
        "authProvider": "LOCAL",
        "createdAt": "2026-08-01T07:15:00.000Z",
        "updatedAt": "2026-08-01T07:15:00.000Z"
      },
      "tokens": {
        "accessToken": "eyJhbGciOi...",
        "refreshToken": "eyJhbGciOi..."
      }
    }
  }
  ```

---

#### 4.2.3 Google Sign-In
Authenticate or register using Google OAuth2 ID Token from Android app.

- **Method:** `POST`
- **Path:** `/api/v1/auth/google`
- **Authentication:** None
- **Request Body:**
  ```json
  {
    "idToken": "eyJhbGciOiJSUzI1NiIs..."
  }
  ```

---

#### 4.2.4 Refresh Access Token
Obtain a fresh pair of access and refresh tokens using a valid Refresh Token.

- **Method:** `POST`
- **Path:** `/api/v1/auth/refresh`
- **Authentication:** None
- **Request Body:**
  ```json
  {
    "refreshToken": "eyJhbGciOi..."
  }
  ```

---

#### 4.2.5 Logout User
Revoke current user token / session.

- **Method:** `POST`
- **Path:** `/api/v1/auth/logout`
- **Authentication:** Bearer Token (Optional body with `refreshToken`)

---

### 4.3 User Profile Endpoints (`/api/v1/users`)

#### 4.3.1 Get Current User Profile
Retrieve profile data for the authenticated user.

- **Method:** `GET`
- **Path:** `/api/v1/users/me`
- **Authentication:** `Bearer <accessToken>`

---

#### 4.3.2 Update Current User Profile
Update personal information for the authenticated user.

- **Method:** `PUT`
- **Path:** `/api/v1/users/me`
- **Authentication:** `Bearer <accessToken>`

---

### 4.4 Habit Chains Endpoints (`/api/v1/chains`)

#### 4.4.1 Create Habit Chain
Create a new habit chain with dual classification (`SPIRITUAL_MORAL` vs `LIFESTYLE_PRODUCTIVITY`).

- **Method:** `POST`
- **Path:** `/api/v1/chains`
- **Authentication:** `Bearer <accessToken>`
- **Request Body:**
  ```json
  {
    "title": "Quit Vaping",
    "description": "Overcoming nicotine addiction through daily mindfulness and substitute breathing.",
    "category": "SPIRITUAL_MORAL",
    "privacyLevel": "LEVEL_0_PRIVATE",
    "targetStartDate": "2026-08-01T12:00:00",
    "costPerInstance": 15.50,
    "timeMinutesPerInstance": 30,
    "triggerTags": ["Stress", "Late Night", "Social Environment"],
    "substituteAction": "Perform 2 Raka'at Prayer / 2-minute Box Breathing and drink cold water",
    "intentStatement": "I intend for the sake of Allah to purify my body and soul from harmful dependencies."
  }
  ```

- **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "message": "Habit chain created successfully",
    "data": {
      "id": "c0a80069-9fb9-1430-819f-b97453210000",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Quit Vaping",
      "description": "Overcoming nicotine addiction through daily mindfulness and substitute breathing.",
      "category": "SPIRITUAL_MORAL",
      "privacyLevel": "LEVEL_0_PRIVATE",
      "status": "ACTIVE",
      "targetStartDate": "2026-08-01T12:00:00",
      "costPerInstance": 15.50,
      "timeMinutesPerInstance": 30,
      "triggerTags": ["Stress", "Late Night", "Social Environment"],
      "substituteAction": "Perform 2 Raka'at Prayer / 2-minute Box Breathing and drink cold water",
      "intentStatement": "I intend for the sake of Allah to purify my body and soul from harmful dependencies.",
      "createdAt": "2026-08-01T12:00:00",
      "updatedAt": "2026-08-01T12:00:00"
    }
  }
  ```

---

#### 4.4.2 Get User Habit Chains
List all habit chains for the logged-in user. Optional `status` filter query parameter (`ACTIVE`, `ARCHIVED`, `GRADUATED`).

- **Method:** `GET`
- **Path:** `/api/v1/chains` or `/api/v1/chains?status=ACTIVE`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": "c0a80069-9fb9-1430-819f-b97453210000",
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "title": "Quit Vaping",
        "description": "Overcoming nicotine addiction through daily mindfulness and substitute breathing.",
        "category": "SPIRITUAL_MORAL",
        "privacyLevel": "LEVEL_0_PRIVATE",
        "status": "ACTIVE",
        "targetStartDate": "2026-08-01T12:00:00",
        "costPerInstance": 15.50,
        "timeMinutesPerInstance": 30,
        "triggerTags": ["Stress", "Late Night", "Social Environment"],
        "substituteAction": "Perform 2 Raka'at Prayer / 2-minute Box Breathing and drink cold water",
        "intentStatement": "I intend for the sake of Allah to purify my body and soul from harmful dependencies.",
        "createdAt": "2026-08-01T12:00:00",
        "updatedAt": "2026-08-01T12:00:00"
      }
    ]
  }
  ```

---

#### 4.4.3 Get Single Habit Chain
Retrieve details for a specific habit chain owned by the authenticated user.

- **Method:** `GET`
- **Path:** `/api/v1/chains/{id}`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "id": "c0a80069-9fb9-1430-819f-b97453210000",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "title": "Quit Vaping",
      "description": "Overcoming nicotine addiction through daily mindfulness and substitute breathing.",
      "category": "SPIRITUAL_MORAL",
      "privacyLevel": "LEVEL_0_PRIVATE",
      "status": "ACTIVE",
      "targetStartDate": "2026-08-01T12:00:00",
      "costPerInstance": 15.50,
      "timeMinutesPerInstance": 30,
      "triggerTags": ["Stress", "Late Night"],
      "substituteAction": "Perform 2 Raka'at Prayer / 2-minute Box Breathing",
      "intentStatement": "I intend for the sake of Allah to purify my body and soul.",
      "createdAt": "2026-08-01T12:00:00",
      "updatedAt": "2026-08-01T12:00:00"
    }
  }
  ```

- **Error Responses:**
  - `404 Not Found` (`NOT_FOUND`) if chain does not exist or belongs to another user.

---

#### 4.4.4 Update Habit Chain
Update metadata, category, privacy level, status, or triggers for a habit chain.

- **Method:** `PUT`
- **Path:** `/api/v1/chains/{id}`
- **Authentication:** `Bearer <accessToken>`
- **Request Body:**
  ```json
  {
    "title": "Quit Vaping & Nicotine",
    "privacyLevel": "LEVEL_1_STREAK_ONLY",
    "status": "ACTIVE",
    "costPerInstance": 20.00
  }
  ```

- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Habit chain updated successfully",
    "data": { ... }
  }
  ```

---

#### 4.4.5 Delete Habit Chain
Delete a habit chain. Cascades via PostgreSQL foreign keys to purge all dependent logs, SOS sessions, and notes with zero orphaned records.

- **Method:** `DELETE`
- **Path:** `/api/v1/chains/{id}`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Habit chain deleted successfully",
    "data": null
  }
  ```
