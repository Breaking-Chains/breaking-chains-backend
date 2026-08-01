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
- `GET /health` | Health check

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

### 4.6 Emergency SOS Endpoints (`/api/v1/emergency`)
- `POST /api/v1/emergency/start` | Initiate SOS urge-surfing session
- `POST /api/v1/emergency/{sessionId}/complete` | Complete SOS session
- `GET /api/v1/emergency/history` | Get SOS panic button history

### 4.7 Confidential Guidance & Counsel Endpoints (`/api/v1/partners`)
- `POST /api/v1/chains/{id}/partners/invite` | Generate encrypted invite code (e.g. `SUHBAH-A1B2C3`)
- `POST /api/v1/partners/accept` | Accept partner invite
- `GET /api/v1/partners/mentees` | Get mentees list
- `POST /api/v1/chains/{id}/counsel-notes` | Submit mentor counsel note (*Nasiha*)
- `GET /api/v1/chains/{id}/counsel-notes` | Get counsel notes
- `POST /api/v1/partners/distress-alert` | Trigger partner distress alert

---

### 4.8 Analytics, Milestones & Barakah Endpoints

#### 4.8.1 Get Habit Chain Analytics & Barakah Impact
Retrieve comprehensive analytics for a habit chain, including clean percentage (resilience score), current/longest streak, money/time saved, *Sadaqah* potential, trigger frequency maps, and earned milestones.

- **Method:** `GET`
- **Path:** `/api/v1/chains/{id}/analytics`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "chainId": "c0a80069-9fb9-1430-819f-b97453210000",
      "title": "Pure Path - Overcome PMO",
      "category": "SPIRITUAL_MORAL",
      "subCategory": "PMO_RECOVERY",
      "totalDaysTracked": 30,
      "totalCleanDays": 28,
      "totalSlipUps": 2,
      "cleanPercentage": 93.33,
      "currentStreakDays": 14,
      "longestStreakDays": 14,
      "moneySaved": 150.00,
      "timeSavedHours": 21.0,
      "sadaqahPotential": 150.00,
      "triggerBreakdown": {
        "Boredom & Social Media Scrolling": 2,
        "Late Night in Bed": 5,
        "Stress": 3
      },
      "earnedMilestones": [
        {
          "badgeId": "d4e5f6a7-b8c9-0123-def0-4567890abcde",
          "chainId": "c0a80069-9fb9-1430-819f-b97453210000",
          "badgeType": "NAFS_LAWWAMAH_STRIVER",
          "title": "7-Day Dopamine Reset Foundation (Nafs al-Lawwamah)",
          "description": "1 week clean! Brain dopamine receptor sensitivity is restoring.",
          "achievedAt": "2026-08-01T12:00:00"
        },
        {
          "badgeId": "e5f6a7b8-c9d0-1234-ef01-567890abcdef",
          "chainId": "c0a80069-9fb9-1430-819f-b97453210000",
          "badgeType": "NAFS_AMMARAH_SURVIVOR",
          "title": "3-Day Withdrawal Survivor (Nafs al-Ammarah)",
          "description": "Survived the initial acute physical urge peak.",
          "achievedAt": "2026-08-01T12:00:00"
        }
      ]
    }
  }
  ```

---

#### 4.8.2 Get All User Milestones & Neuroplasticity Badges
Retrieve all earned neuroplasticity badges and spiritual milestones across all habit chains for the logged-in user.

- **Method:** `GET`
- **Path:** `/api/v1/milestones`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": [
      {
        "badgeId": "d4e5f6a7-b8c9-0123-def0-4567890abcde",
        "chainId": "c0a80069-9fb9-1430-819f-b97453210000",
        "badgeType": "NAFS_LAWWAMAH_STRIVER",
        "title": "7-Day Dopamine Reset Foundation (Nafs al-Lawwamah)",
        "description": "1 week clean! Brain dopamine receptor sensitivity is restoring.",
        "achievedAt": "2026-08-01T12:00:00"
      }
    ]
  }
  ```
