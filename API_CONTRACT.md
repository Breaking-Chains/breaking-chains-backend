# Breaking Chains - API Specification Contract

**Version:** 1.0.0  
**Base URL:** `http://localhost:5000/api/v1` (Development)  
**Content-Type:** `application/json`

---

## Table of Contents
1. [Authentication Overview](#authentication-overview)
2. [Error Handling & Response Format](#error-handling--response-format)
3. [Endpoints](#endpoints)
   - [1. Register User](#1-register-user)
   - [2. Login (Email & Password)](#2-login-email--password)
   - [3. Google Sign-In](#3-google-sign-in)
   - [4. Refresh Access Token](#4-refresh-access-token)
   - [5. Logout](#5-logout)
   - [6. Get Current User Profile](#6-get-current-user-profile)
   - [7. Update User Profile](#7-update-user-profile)
4. [Data Models & Types](#data-models--types)

---

## Authentication Overview

Protected endpoints require a Bearer token in the `Authorization` HTTP header:

```http
Authorization: Bearer <accessToken>
```

- **AccessToken Expiry:** 15 minutes
- **RefreshToken Expiry:** 7 days

When an API call returns a `401 Unauthorized` status due to token expiration (`TOKEN_EXPIRED`), the frontend client should call the `/auth/refresh` endpoint to obtain a new `accessToken` using the stored `refreshToken`.

---

## Error Handling & Response Format

### Success Response Envelope
All successful HTTP responses use a `2xx` status code and return JSON formatted as:

```json
{
  "status": "success",
  "data": { ... }
}
```

### Error Response Envelope
All failing HTTP responses use standard HTTP status codes (`4xx` or `5xx`) and return JSON formatted as:

```json
{
  "status": "error",
  "code": "ERROR_CODE_NAME",
  "message": "Human readable error description",
  "details": null
}
```

#### Common Error Codes
| Code | Status Code | Description |
| :--- | :--- | :--- |
| `VALIDATION_ERROR` | 400 | Payload failed schema validation rules. |
| `INVALID_CREDENTIALS` | 401 | Email or password incorrect. |
| `UNAUTHORIZED` | 401 | Access token missing or invalid. |
| `TOKEN_EXPIRED` | 401 | Access token has expired. |
| `INVALID_REFRESH_TOKEN` | 401 | Refresh token is invalid or revoked. |
| `USER_EXISTS` | 409 | Email or username is already registered. |
| `NOT_FOUND` | 404 | Resource requested does not exist. |
| `INTERNAL_SERVER_ERROR` | 500 | Server error. |

---

## Endpoints

### 1. Register User
Creates a new user account with email and password.

- **URL:** `/auth/register`
- **Method:** `POST`
- **Auth Required:** No

#### Request Body
```json
{
  "email": "john.doe@example.com",
  "password": "Password123!",
  "fullName": "John Doe",
  "username": "johndoe"
}
```

#### Response `201 Created`
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "usr_c1234567890",
      "email": "john.doe@example.com",
      "fullName": "John Doe",
      "username": "johndoe",
      "avatarUrl": null,
      "bio": null,
      "authProvider": "LOCAL",
      "createdAt": "2026-07-31T06:50:00.000Z",
      "updatedAt": "2026-07-31T06:50:00.000Z"
    },
    "tokens": {
      "accessToken": "eyJhbGciOi...",
      "refreshToken": "eyJhbGciOi..."
    }
  }
}
```

#### Errors
- `400 Bad Request`: `VALIDATION_ERROR` (e.g. invalid email format, password under 8 characters, or missing fields)
- `409 Conflict`: `USER_EXISTS` ("Email or username already in use")

---

### 2. Login (Email & Password)
Authenticates a user with email and password credentials.

- **URL:** `/auth/login`
- **Method:** `POST`
- **Auth Required:** No

#### Request Body
```json
{
  "email": "john.doe@example.com",
  "password": "Password123!"
}
```

#### Response `200 OK`
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "usr_c1234567890",
      "email": "john.doe@example.com",
      "fullName": "John Doe",
      "username": "johndoe",
      "avatarUrl": null,
      "bio": null,
      "authProvider": "LOCAL",
      "createdAt": "2026-07-31T06:50:00.000Z",
      "updatedAt": "2026-07-31T06:50:00.000Z"
    },
    "tokens": {
      "accessToken": "eyJhbGciOi...",
      "refreshToken": "eyJhbGciOi..."
    }
  }
}
```

#### Errors
- `401 Unauthorized`: `INVALID_CREDENTIALS` ("Invalid email or password")

---

### 3. Google Sign-In
Authenticates or registers a user via Google OAuth ID Token.

- **URL:** `/auth/google`
- **Method:** `POST`
- **Auth Required:** No

#### Request Body
```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIs..."
}
```
*Note: `idToken` is the Google ID Token returned by Google Identity Services / Android Google Sign-In SDK.*

#### Response `200 OK` (Existing user) / `201 Created` (New Google user)
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "usr_g9876543210",
      "email": "jane.doe@gmail.com",
      "fullName": "Jane Doe",
      "username": "jane_doe_g",
      "avatarUrl": "https://lh3.googleusercontent.com/a/...",
      "bio": null,
      "authProvider": "GOOGLE",
      "createdAt": "2026-07-31T06:50:00.000Z",
      "updatedAt": "2026-07-31T06:50:00.000Z"
    },
    "tokens": {
      "accessToken": "eyJhbGciOi...",
      "refreshToken": "eyJhbGciOi..."
    }
  }
}
```

#### Errors
- `401 Unauthorized`: `UNAUTHORIZED` ("Invalid or expired Google ID token")

---

### 4. Refresh Access Token
Obtains a fresh access token using a valid refresh token.

- **URL:** `/auth/refresh`
- **Method:** `POST`
- **Auth Required:** No

#### Request Body
```json
{
  "refreshToken": "eyJhbGciOi..."
}
```

#### Response `200 OK`
```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "eyJhbGciOi..."
  }
}
```

#### Errors
- `401 Unauthorized`: `INVALID_REFRESH_TOKEN` ("Refresh token is invalid or expired")

---

### 5. Logout
Revokes the user's refresh token.

- **URL:** `/auth/logout`
- **Method:** `POST`
- **Auth Required:** Yes (`Authorization: Bearer <accessToken>`)

#### Request Body
```json
{
  "refreshToken": "eyJhbGciOi..."
}
```

#### Response `200 OK`
```json
{
  "status": "success",
  "message": "Logged out successfully"
}
```

---

### 6. Get Current User Profile
Retrieves the logged-in user's profile details.

- **URL:** `/users/me`
- **Method:** `GET`
- **Auth Required:** Yes (`Authorization: Bearer <accessToken>`)

#### Request Headers
```http
Authorization: Bearer eyJhbGciOi...
```

#### Response `200 OK`
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "usr_c1234567890",
      "email": "john.doe@example.com",
      "fullName": "John Doe",
      "username": "johndoe",
      "avatarUrl": "https://example.com/avatars/john.jpg",
      "bio": "Breaking bad habits, one day at a time.",
      "authProvider": "LOCAL",
      "createdAt": "2026-07-31T06:50:00.000Z",
      "updatedAt": "2026-07-31T06:50:00.000Z"
    }
  }
}
```

---

### 7. Update User Profile
Updates the profile information of the logged-in user. All fields are optional.

- **URL:** `/users/me`
- **Method:** `PUT`
- **Auth Required:** Yes (`Authorization: Bearer <accessToken>`)

#### Request Body
```json
{
  "fullName": "John Updated",
  "username": "john_updated",
  "bio": "Updated bio text",
  "avatarUrl": "https://example.com/new_avatar.jpg"
}
```

#### Response `200 OK`
```json
{
  "status": "success",
  "data": {
    "user": {
      "id": "usr_c1234567890",
      "email": "john.doe@example.com",
      "fullName": "John Updated",
      "username": "john_updated",
      "avatarUrl": "https://example.com/new_avatar.jpg",
      "bio": "Updated bio text",
      "authProvider": "LOCAL",
      "createdAt": "2026-07-31T06:50:00.000Z",
      "updatedAt": "2026-07-31T06:55:00.000Z"
    }
  }
}
```

#### Errors
- `400 Bad Request`: `VALIDATION_ERROR`
- `409 Conflict`: `USER_EXISTS` ("Username already taken")

---

## Data Models & Types

### TypeScript Interfaces for Frontend Client

```typescript
export type AuthProvider = 'LOCAL' | 'GOOGLE';

export interface User {
  id: string;
  email: string;
  fullName: string;
  username: string;
  avatarUrl: string | null;
  bio: string | null;
  authProvider: AuthProvider;
  createdAt: string; // ISO 8601 string
  updatedAt: string; // ISO 8601 string
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

export interface AuthResponse {
  user: User;
  tokens: AuthTokens;
}

export interface ApiResponse<T> {
  status: 'success' | 'error';
  data?: T;
  code?: string;
  message?: string;
  details?: any;
}
```
