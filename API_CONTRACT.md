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

---

### 4.7 Confidential Guidance & Mentorship Endpoints (`/api/v1/partners`)

#### 4.7.1 Generate Partner Invite Code
- `POST /api/v1/chains/{id}/partners/invite`

#### 4.7.2 Accept Partner Invite Code
- `POST /api/v1/partners/accept`

#### 4.7.3 Get Mentees List
- `GET /api/v1/partners/mentees`

#### 4.7.4 Submit Mentor Counsel Note (*Nasiha*)
- `POST /api/v1/chains/{id}/counsel-notes` (Restricted to accepted mentors)

#### 4.7.5 Get Chain Counsel Notes
- `GET /api/v1/chains/{id}/counsel-notes`

#### 4.7.6 Send 2-Way Mentorship Chat Message
Send a direct message in an active mentorship partnership thread between student and mentor.

- **Method:** `POST`
- **Path:** `/api/v1/partnerships/{partnershipId}/messages`
- **Authentication:** `Bearer <accessToken>`
- **Request Body:**
  ```json
  {
    "messageContent": "Assalamu Alaikum, I completed my 7-day clean streak today and feel much stronger!"
  }
  ```

- **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "message": "Chat message sent successfully",
    "data": {
      "id": "f5e4d3c2-b1a0-9876-fedc-5432109876ba",
      "partnershipId": "b2c3d4e5-f6a7-8901-bcde-234567890abc",
      "senderId": "c0a80069-9fb9-1430-819f-b97453210000",
      "senderFullName": "Alex Smith",
      "senderUsername": "alexsmith",
      "messageContent": "Assalamu Alaikum, I completed my 7-day clean streak today and feel much stronger!",
      "isRead": false,
      "createdAt": "2026-08-01T12:00:00"
    }
  }
  ```

---

#### 4.7.7 Get 2-Way Mentorship Chat History
Retrieve all chat messages in a partnership thread (in chronological order) and automatically mark incoming messages as read.

- **Method:** `GET`
- **Path:** `/api/v1/partnerships/{partnershipId}/messages`
- **Authentication:** `Bearer <accessToken>`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": [
      {
        "id": "f5e4d3c2-b1a0-9876-fedc-5432109876ba",
        "partnershipId": "b2c3d4e5-f6a7-8901-bcde-234567890abc",
        "senderId": "c0a80069-9fb9-1430-819f-b97453210000",
        "senderFullName": "Alex Smith",
        "senderUsername": "alexsmith",
        "messageContent": "Assalamu Alaikum, I completed my 7-day clean streak today and feel much stronger!",
        "isRead": true,
        "createdAt": "2026-08-01T12:00:00"
      }
    ]
  }
  ```

---

### 4.8 Analytics, Milestones & Barakah Endpoints
- `GET /api/v1/chains/{id}/analytics`
- `GET /api/v1/milestones`
