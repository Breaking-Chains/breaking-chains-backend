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

### 4.1 System Health Check (`/health`)
- **Method:** `GET` | **Auth:** None

### 4.2 Authentication Endpoints (`/api/v1/auth`)
- `POST /api/v1/auth/register` | Register user
- `POST /api/v1/auth/login` | Local login
- `POST /api/v1/auth/google` | Google OAuth Sign-In
- `POST /api/v1/auth/refresh` | Refresh token rotation
- `POST /api/v1/auth/logout` | Revoke refresh token

### 4.3 User Profile Endpoints (`/api/v1/users`)
- `GET /api/v1/users/me` | Get profile
- `PUT /api/v1/users/me` | Update profile

### 4.4 Habit Chains Endpoints (`/api/v1/chains`)
- `POST /api/v1/chains` | Create habit chain
- `GET /api/v1/chains` | Get user chains (`?status=ACTIVE`)
- `GET /api/v1/chains/{id}` | Get chain by ID
- `PUT /api/v1/chains/{id}` | Update chain
- `DELETE /api/v1/chains/{id}` | Delete chain (Cascades ON DELETE CASCADE)

### 4.5 Check-In & Resilience Endpoints (`/api/v1/chains/{id}/logs`)
- `POST /api/v1/chains/{id}/logs` | Submit check-in log (`CLEAN`, `URGE_RESISTED`, `SLIP_UP`)
- `GET /api/v1/chains/{id}/logs` | Get check-in history
- `DELETE /api/v1/chains/{id}/logs/{logId}` | Delete log entry

---

### 4.6 Emergency SOS Endpoints (`/api/v1/emergency`)

#### 4.6.1 Start Emergency SOS Session
Initiate a 1-tap emergency intervention. Returns physical circuit breakers, Wudu protocols, Ayat/Du'a spiritual shields, and a 60s urge-surfing box breathing timer.

- **Method:** `POST`
- **Path:** `/api/v1/emergency/start`
- **Authentication:** `Bearer <accessToken>`
- **Request Body:**
  ```json
  {
    "chainId": "c0a80069-9fb9-1430-819f-b97453210000",
    "sessionType": "PHYSICAL_CIRCUIT_BREAKER",
    "cravingBefore": 9
  }
  ```

- **Response `201 Created` (PMO/Spiritual Recovery Content):**
  ```json
  {
    "status": "success",
    "message": "Emergency SOS session started",
    "data": {
      "sessionId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
      "chainId": "c0a80069-9fb9-1430-819f-b97453210000",
      "sessionType": "PHYSICAL_CIRCUIT_BREAKER",
      "title": "🚨 BREAK THE LOOP — EMERGENCY INTERVENTION",
      "subtitle": "Urges peak over 15 to 20 minutes like a wave. Execute these physical circuit breakers right now.",
      "immediatePhysicalStep": "🚨 STEP 1: Stand up, leave your bed and current room immediately. Put your phone down.",
      "waterProtocolStep": "💧 STEP 2: Go to the bathroom and wash your face with cool water or perform a full Wudu.",
      "spiritualShield": "📖 SPIRITUAL SHIELD: Recite Ayat al-Kursi (2:255), Surah An-Nur verse 30 ('Tell believing men to lower their gaze and guard their chastity'), and say 'A'udhu billahi mina ash-shaytani ar-rajim'.",
      "breathingTimerSeconds": 60,
      "groundingSteps": [
        "5 things you can physically see around you right now",
        "4 things you can physically feel or touch",
        "3 distinct sounds you can hear in your environment",
        "2 scents you can smell",
        "1 slow, deep diaphragmatic breath in"
      ],
      "cravingBefore": 9,
      "createdAt": "2026-08-01T12:00:00"
    }
  }
  ```

---

#### 4.6.2 Complete Emergency SOS Session
Record final craving drop delta, techniques applied, and total session duration.

- **Method:** `POST`
- **Path:** `/api/v1/emergency/{sessionId}/complete`
- **Authentication:** `Bearer <accessToken>`
- **Request Body:**
  ```json
  {
    "cravingAfter": 3,
    "durationSeconds": 180,
    "techniqueUsed": "PHYSICAL_LEAVE_ROOM + WUDU_COOL_WATER + BOX_BREATHING_60S"
  }
  ```

- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Emergency session completed successfully",
    "data": {
      "id": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
      "sessionType": "PHYSICAL_CIRCUIT_BREAKER",
      "techniqueUsed": "PHYSICAL_LEAVE_ROOM + WUDU_COOL_WATER + BOX_BREATHING_60S",
      "cravingBefore": 9,
      "cravingAfter": 3,
      "durationSeconds": 180,
      "createdAt": "2026-08-01T12:00:00"
    }
  }
  ```

---

#### 4.6.3 Get Emergency SOS History
Retrieve all past emergency panic button sessions and craving reduction metrics for the user.

- **Method:** `GET`
- **Path:** `/api/v1/emergency/history`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
        "sessionType": "PHYSICAL_CIRCUIT_BREAKER",
        "techniqueUsed": "PHYSICAL_LEAVE_ROOM + WUDU_COOL_WATER + BOX_BREATHING_60S",
        "cravingBefore": 9,
        "cravingAfter": 3,
        "durationSeconds": 180,
        "createdAt": "2026-08-01T12:00:00"
      }
    ]
  }
  ```
