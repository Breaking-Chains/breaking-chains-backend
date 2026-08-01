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

#### 4.2.2 Login User
Authenticate using email and password.

- **Method:** `POST`
- **Path:** `/api/v1/auth/login`
- **Authentication:** None

#### 4.2.3 Google Sign-In
Authenticate or register using Google OAuth2 ID Token from Android app.

- **Method:** `POST`
- **Path:** `/api/v1/auth/google`
- **Authentication:** None

#### 4.2.4 Refresh Access Token
Obtain a fresh pair of access and refresh tokens using a valid Refresh Token.

- **Method:** `POST`
- **Path:** `/api/v1/auth/refresh`
- **Authentication:** None

#### 4.2.5 Logout User
Revoke current user token / session.

- **Method:** `POST`
- **Path:** `/api/v1/auth/logout`
- **Authentication:** Bearer Token

---

### 4.3 User Profile Endpoints (`/api/v1/users`)

#### 4.3.1 Get Current User Profile
- **Method:** `GET`
- **Path:** `/api/v1/users/me`
- **Authentication:** `Bearer <accessToken>`

#### 4.3.2 Update Current User Profile
- **Method:** `PUT`
- **Path:** `/api/v1/users/me`
- **Authentication:** `Bearer <accessToken>`

---

### 4.4 Habit Chains Endpoints (`/api/v1/chains`)

#### 4.4.1 Create Habit Chain
- **Method:** `POST`
- **Path:** `/api/v1/chains`
- **Authentication:** `Bearer <accessToken>`

#### 4.4.2 Get User Habit Chains
- **Method:** `GET`
- **Path:** `/api/v1/chains` or `/api/v1/chains?status=ACTIVE`
- **Authentication:** `Bearer <accessToken>`

#### 4.4.3 Get Single Habit Chain
- **Method:** `GET`
- **Path:** `/api/v1/chains/{id}`
- **Authentication:** `Bearer <accessToken>`

#### 4.4.4 Update Habit Chain
- **Method:** `PUT`
- **Path:** `/api/v1/chains/{id}`
- **Authentication:** `Bearer <accessToken>`

#### 4.4.5 Delete Habit Chain
- **Method:** `DELETE`
- **Path:** `/api/v1/chains/{id}`
- **Authentication:** `Bearer <accessToken>`

---

### 4.5 Check-In & Resilience Endpoints (`/api/v1/chains/{id}/logs`)

#### 4.5.1 Log Daily Check-In
Record a daily check-in (`CLEAN`, `URGE_RESISTED`, or `SLIP_UP`). Returns real-time resilience metrics and category-specific post-slip guidance.

- **Method:** `POST`
- **Path:** `/api/v1/chains/{id}/logs`
- **Authentication:** `Bearer <accessToken>`
- **Request Body (Clean / Resisted Urge Example):**
  ```json
  {
    "status": "CLEAN",
    "intensityLevel": 2,
    "triggerTag": "Late Night in Bed",
    "reflectionNote": "Resisted urge by doing Wudu and box breathing."
  }
  ```

- **Request Body (Slip-Up Example):**
  ```json
  {
    "status": "SLIP_UP",
    "intensityLevel": 8,
    "triggerTag": "Boredom & Social Media Scrolling",
    "reflectionNote": "Stayed in bed too long with phone."
  }
  ```

- **Response `201 Created` (with PMO/Spiritual Post-Slip Guidance Payload):**
  ```json
  {
    "status": "success",
    "message": "Check-in logged successfully",
    "data": {
      "id": "f8a7b6c5-d4e3-2109-8765-432109876543",
      "chainId": "c0a80069-9fb9-1430-819f-b97453210000",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "logTimestamp": "2026-08-01T12:00:00",
      "status": "SLIP_UP",
      "intensityLevel": 8,
      "triggerTag": "Boredom & Social Media Scrolling",
      "reflectionNote": "Stayed in bed too long with phone.",
      "goodDeedDone": null,
      "chaserAlertActive": true,
      "currentStreakDays": 0,
      "longestStreakDays": 14,
      "totalCleanDays": 14,
      "totalDays": 15,
      "resilienceScore": 93.33,
      "postSlipGuidance": {
        "title": "Renew Your Intent (Niyyah) & Stand Up Immediately",
        "subtitle": "A slip is a temporary detour, not an identity collapse. Turn to Allah right now with hope.",
        "spiritualRemind": "O My servants who have transgressed against themselves, do not despair of the mercy of Allah. Indeed, Allah forgives all sins. (Surah Az-Zumar 39:53)",
        "immediateAction": "1. Leave your bed/room immediately.\n2. Perform Wudu with cool water.\n3. Pray 2 Raka'at Salat al-Tawbah.",
        "charitySuggestion": "Donate $1 to $5 as Sadaqah to erase this mistake with a good deed (Al-Hasanat yudhibna al-sayyi'at).",
        "chaserEffectWarning": "⚠️ 48-Hour Chaser-Effect Caution: Dopamine levels are depleted right now. Urges will peak over the next 48 hours. Keep your environment clean and leave your phone outside your bedroom tonight.",
        "routineSwapSuggestion": "Substitute Routine: Perform 2 Raka'at Prayer / 2-minute Box Breathing and drink cold water"
      },
      "createdAt": "2026-08-01T12:00:00"
    }
  }
  ```

---

#### 4.5.2 Get Chain Check-In Logs
Retrieve chronological log entries for a habit chain.

- **Method:** `GET`
- **Path:** `/api/v1/chains/{id}/logs`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": "f8a7b6c5-d4e3-2109-8765-432109876543",
        "chainId": "c0a80069-9fb9-1430-819f-b97453210000",
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "logTimestamp": "2026-08-01T12:00:00",
        "status": "CLEAN",
        "intensityLevel": 2,
        "triggerTag": "Late Night in Bed",
        "reflectionNote": "Resisted urge by doing Wudu and box breathing.",
        "chaserAlertActive": false,
        "createdAt": "2026-08-01T12:00:00"
      }
    ]
  }
  ```

---

#### 4.5.3 Delete Check-In Log
Delete a specific check-in log entry.

- **Method:** `DELETE`
- **Path:** `/api/v1/chains/{id}/logs/{logId}`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Check-in log deleted successfully",
    "data": null
  }
  ```
