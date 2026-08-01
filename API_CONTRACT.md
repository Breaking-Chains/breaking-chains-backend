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
| `401 Unauthorized` | `INVALID_CREDENTIALS` | Incorrect email or password provided during login. |
| `401 Unauthorized` | `UNAUTHORIZED` | Missing, malformed, or expired JWT Access Token in `Authorization` header. |
| `401 Unauthorized` | `TOKEN_EXPIRED` | JWT Access Token has expired. |
| `401 Unauthorized` | `INVALID_REFRESH_TOKEN` | Refresh Token is invalid, expired, or corrupted. |
| `404 Not Found` | `NOT_FOUND` | Requested user or resource does not exist. |
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
  *Validation Rules:*
  - `email`: Required, valid email format.
  - `password`: Required, minimum 8 characters.
  - `fullName`: Required, non-blank.
  - `username`: Required, non-blank.

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

- **Error Responses:**
  - `400 Bad Request` (`VALIDATION_ERROR`)
  - `409 Conflict` (`USER_EXISTS`)

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

- **Error Responses:**
  - `400 Bad Request` (`VALIDATION_ERROR`)
  - `401 Unauthorized` (`INVALID_CREDENTIALS`)

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

- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "user": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "email": "john.doe@gmail.com",
        "fullName": "John Doe",
        "username": "john_doe_gmail_com",
        "avatarUrl": "https://lh3.googleusercontent.com/a/...",
        "bio": null,
        "authProvider": "GOOGLE",
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

- **Error Responses:**
  - `401 Unauthorized` (`UNAUTHORIZED` / `INVALID_TOKEN`)

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

- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "accessToken": "eyJhbGciOi...",
      "refreshToken": "eyJhbGciOi..."
    }
  }
  ```

- **Error Responses:**
  - `401 Unauthorized` (`INVALID_REFRESH_TOKEN`)

---

#### 4.2.5 Logout User
Revoke current user token / session.

- **Method:** `POST`
- **Path:** `/api/v1/auth/logout`
- **Authentication:** None (Optional body with `refreshToken`)
- **Request Body (Optional):**
  ```json
  {
    "refreshToken": "eyJhbGciOi..."
  }
  ```

- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Logged out successfully"
  }
  ```

---

### 4.3 User Profile Endpoints (`/api/v1/users`)

#### 4.3.1 Get Current User Profile
Retrieve profile data for the authenticated user.

- **Method:** `GET`
- **Path:** `/api/v1/users/me`
- **Authentication:** `Bearer <accessToken>`
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
        "avatarUrl": "https://example.com/avatar.jpg",
        "bio": "Overcoming obstacles every day.",
        "authProvider": "LOCAL",
        "createdAt": "2026-08-01T07:15:00.000Z",
        "updatedAt": "2026-08-01T07:15:00.000Z"
      }
    }
  }
  ```

- **Error Responses:**
  - `401 Unauthorized` (`UNAUTHORIZED`)

---

#### 4.3.2 Update Current User Profile
Update personal information for the authenticated user.

- **Method:** `PUT`
- **Path:** `/api/v1/users/me`
- **Authentication:** `Bearer <accessToken>`
- **Request Body:**
  ```json
  {
    "fullName": "John Updated Doe",
    "username": "john_updated",
    "bio": "New bio description.",
    "avatarUrl": "https://example.com/new-avatar.jpg"
  }
  ```
  *Note:* All fields are optional.

- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "user": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "email": "john.doe@example.com",
        "fullName": "John Updated Doe",
        "username": "john_updated",
        "avatarUrl": "https://example.com/new-avatar.jpg",
        "bio": "New bio description.",
        "authProvider": "LOCAL",
        "createdAt": "2026-08-01T07:15:00.000Z",
        "updatedAt": "2026-08-01T07:20:00.000Z"
      }
    }
  }
  ```

- **Error Responses:**
  - `401 Unauthorized` (`UNAUTHORIZED`)
  - `409 Conflict` (`USER_EXISTS`)
