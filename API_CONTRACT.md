# Breaking Chains Backend - REST API Specification & Contract

**Version:** `1.0.0`  
**Framework:** Spring Boot 3 (Java 17)  
**Base URL (Local):** `http://localhost:8080`  
**Base URL (Production):** `https://breaking-chains-backend.onrender.com`  
**API Version Prefix:** `/api/v1`  
**Content-Type:** `application/json`

---

## 1. Architecture & Global Standards

### 1.1 Authentication & Security Header
All protected endpoints require an HTTP `Authorization` bearer token header:
```http
Authorization: Bearer <accessToken>
```

---

### 1.2 Response Envelopes

#### 1.2.1 Success Response (`ApiResponse<T>`)
All successful API responses return an HTTP `200 OK` or `201 Created` with JSON wrapped in the uniform envelope:

```json
{
  "status": "success",
  "message": "Optional contextual status message (present when applicable)",
  "data": { ... }
}
```

#### 1.2.2 Error Response (`ApiErrorResponse`)
All failed API calls return standard HTTP 4xx/5xx status codes wrapped in the error envelope:

```json
{
  "status": "error",
  "code": "ERROR_CODE_STRING",
  "message": "Human-readable summary of the error",
  "details": {
    "field": "Specific validation failure or constraint description"
  }
}
```

---

### 1.3 Standard Error Codes & HTTP Status Table

| HTTP Status | Error Code | Description |
| :--- | :--- | :--- |
| `400 Bad Request` | `VALIDATION_ERROR` | Request body failed Jakarta `@Valid` constraints (missing required fields, out of range, etc.). |
| `400 Bad Request` | `MALFORMED_JSON` | Required request body is missing or contains malformed JSON syntax. |
| `400 Bad Request` | `INVALID_PARAMETER` | Path variable or parameter type mismatch (e.g. invalid UUID format). |
| `401 Unauthorized` | `INVALID_CREDENTIALS` | Invalid email or password during login. |
| `401 Unauthorized` | `UNAUTHORIZED` | Missing, malformed, or expired JWT Access Token in `Authorization` header. |
| `401 Unauthorized` | `TOKEN_EXPIRED` | JWT Access Token has expired. |
| `401 Unauthorized` | `INVALID_REFRESH_TOKEN` | Refresh Token is invalid, expired, or revoked. |
| `403 Forbidden` | `FORBIDDEN` | Authenticated user lacks permission for the target resource. |
| `404 Not Found` | `NOT_FOUND` | Requested entity (chain, user, log entry, session, mentor profile) does not exist. |
| `405 Method Not Allowed` | `METHOD_NOT_ALLOWED` | HTTP method not supported for target URI path. |
| `409 Conflict` | `USER_EXISTS` | Email or username is already registered. |
| `500 Internal Server Error` | `INTERNAL_SERVER_ERROR` | Unhandled backend application exception. |

---

### 1.4 Data Models & Enums Reference

| Enum Name | Allowed Values | Description |
| :--- | :--- | :--- |
| `ChainStatus` | `ACTIVE`, `ARCHIVED`, `GRADUATED` | Lifecycle status of a habit chain. |
| `CheckInStatus` | `CLEAN`, `URGE_RESISTED`, `PEEKED_EDGED`, `SLIP_UP` | Status submitted during a daily check-in. |
| `EmergencyType` | `SPIRITUAL`, `PSYCHOLOGICAL`, `PHYSICAL_CIRCUIT_BREAKER` | SOS urge-surfing module selected by the user. |
| `HabitCategory` | `SPIRITUAL_MORAL`, `LIFESTYLE_PRODUCTIVITY` | High-level classification of habit target. |
| `HabitSubCategory` | `PMO_RECOVERY`, `SMOKING_VAPING`, `DIGITAL_SCROLLING`, `GENERAL_HABIT` | Specific habit recovery domain. |
| `PrivacyLevel` | `LEVEL_0_PRIVATE`, `LEVEL_1_STREAK_ONLY`, `LEVEL_2_FULL_COUNSEL` | Privacy visibility mode for accountability partners and mentors. |
| `PartnerRole` | `ACCOUNTABILITY_PARTNER`, `SPIRITUAL_MENTOR` | Role assigned to a partner connected to a chain. |
| `PartnershipStatus` | `PENDING`, `ACCEPTED`, `DECLINED`, `REVOKED` | Status of an accountability invitation/link. |
| `MentorStatus` | `PENDING`, `APPROVED`, `REJECTED` | Review status of a verified mentor registration application. |
| `BadgeType` | `NAFS_AMMARAH_SURVIVOR`, `NAFS_LAWWAMAH_STRIVER`, `NEURAL_REWIRE_21`, `HEART_PURITY_40`, `NAFS_MUTMAINNAH_RESET` | Milestone badges awarded for clean streak progression (Day 3, 7, 21, 40, 90). |
| `AuthProvider` | `LOCAL`, `GOOGLE` | Authentication identity provider. |

---

## 2. API Endpoints Specification

---

### 2.1 System Health (`/health`)

#### 2.1.1 Service Health Check
Retrieves operational health and application deployment metadata.

* **Method:** `GET`
* **Path:** `/health`
* **Authentication:** `Public`
* **Response `200 OK`:**
  ```json
  {
    "status": "UP",
    "service": "breaking-chains-backend",
    "framework": "Spring Boot 3 (Java 17)"
  }
  ```

---

### 2.2 Authentication & Session Management (`/api/v1/auth`)

#### 2.2.1 Register User
Creates a new local user account.

* **Method:** `POST`
* **Path:** `/api/v1/auth/register`
* **Authentication:** `Public`
* **Request Body:**
  ```json
  {
    "fullName": "Alex Smith",
    "username": "alexsmith",
    "email": "alex.smith@example.com",
    "password": "SecurePassword123!"
  }
  ```
* **Validation Rules:**
  * `fullName`: Required (`@NotBlank`)
  * `username`: Required (`@NotBlank`), 3 to 30 chars
  * `email`: Required (`@NotBlank`), Valid email format (`@Email`)
  * `password`: Required (`@NotBlank`), Minimum 6 characters (`@Size(min = 6)`)
* **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "data": {
      "user": {
        "id": "c0a80069-9fb9-1430-819f-b97453210000",
        "email": "alex.smith@example.com",
        "username": "alexsmith",
        "fullName": "Alex Smith",
        "avatarUrl": null,
        "authProvider": "LOCAL",
        "isVerifiedMentor": false,
        "createdAt": "2026-08-01T10:00:00"
      },
      "tokens": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "refreshToken": "d8e9f0a1-b2c3-4d5e-6f7a-8b9c0d1e2f3a",
        "tokenType": "Bearer",
        "expiresIn": 86400000
      }
    }
  }
  ```
* **Error Scenarios:** `400 VALIDATION_ERROR`, `409 USER_EXISTS`

#### 2.2.2 User Login
Authenticates local user credentials and issues tokens.

* **Method:** `POST`
* **Path:** `/api/v1/auth/login`
* **Authentication:** `Public`
* **Request Body:**
  ```json
  {
    "email": "alex.smith@example.com",
    "password": "SecurePassword123!"
  }
  ```
* **Validation Rules:**
  * `email`: Required (`@NotBlank`), Valid email format
  * `password`: Required (`@NotBlank`)
* **Response `200 OK`:** Same structure as `AuthDataDto` (returns `user` and `tokens`).
* **Error Scenarios:** `400 VALIDATION_ERROR`, `401 INVALID_CREDENTIALS`

#### 2.2.3 Google OAuth Sign-In
Authenticates or registers a user via Google OAuth ID Token.

* **Method:** `POST`
* **Path:** `/api/v1/auth/google`
* **Authentication:** `Public`
* **Request Body:**
  ```json
  {
    "idToken": "eyJhbGciOiJSUzI1NiIs..."
  }
  ```
* **Validation Rules:**
  * `idToken`: Required (`@NotBlank`)
* **Response `200 OK`:** Same structure as `AuthDataDto`.
* **Error Scenarios:** `400 VALIDATION_ERROR`, `401 UNAUTHORIZED`

#### 2.2.4 Refresh Access Token
Rotates a Refresh Token and generates a new JWT Access Token.

* **Method:** `POST`
* **Path:** `/api/v1/auth/refresh`
* **Authentication:** `Public`
* **Request Body:**
  ```json
  {
    "refreshToken": "d8e9f0a1-b2c3-4d5e-6f7a-8b9c0d1e2f3a"
  }
  ```
* **Validation Rules:**
  * `refreshToken`: Required (`@NotBlank`)
* **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
      "refreshToken": "e9f0a1b2-c3d4-5e6f-7a8b-9c0d1e2f3a4b",
      "tokenType": "Bearer",
      "expiresIn": 86400000
    }
  }
  ```
* **Error Scenarios:** `401 INVALID_REFRESH_TOKEN`

#### 2.2.5 Logout User
Revokes the provided Refresh Token.

* **Method:** `POST`
* **Path:** `/api/v1/auth/logout`
* **Authentication:** `Optional`
* **Request Body:** (Optional)
  ```json
  {
    "refreshToken": "d8e9f0a1-b2c3-4d5e-6f7a-8b9c0d1e2f3a"
  }
  ```
* **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Logged out successfully"
  }
  ```

---

### 2.3 User Profile Management (`/api/v1/users`)

#### 2.3.1 Get Current User Profile
Retrieves authenticated user profile information.

* **Method:** `GET`
* **Path:** `/api/v1/users/me`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "user": {
        "id": "c0a80069-9fb9-1430-819f-b97453210000",
        "email": "alex.smith@example.com",
        "username": "alexsmith",
        "fullName": "Alex Smith",
        "avatarUrl": "https://example.com/avatar.jpg",
        "authProvider": "LOCAL",
        "isVerifiedMentor": false,
        "createdAt": "2026-08-01T10:00:00"
      }
    }
  }
  ```

#### 2.3.2 Update User Profile
Updates user full name or avatar URL.

* **Method:** `PUT`
* **Path:** `/api/v1/users/me`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "fullName": "Alexander Smith",
    "avatarUrl": "https://example.com/new-avatar.jpg"
  }
  ```
* **Response `200 OK`:** Returns updated `AuthDataDto`.

---

### 2.4 Habit Chains Management (`/api/v1/chains`)

#### 2.4.1 Create Habit Chain
Initializes a new habit recovery or streak tracking chain.

* **Method:** `POST`
* **Path:** `/api/v1/chains`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "title": "PMO Freedom & Tazkiyah",
    "description": "Daily commitment to purity of mind and sight.",
    "category": "SPIRITUAL_MORAL",
    "subCategory": "PMO_RECOVERY",
    "targetDays": 90,
    "privacyLevel": "LEVEL_2_FULL_COUNSEL",
    "customPledge": "I pledge to lower my gaze and seek refuge whenever triggered."
  }
  ```
* **Validation Rules:**
  * `title`: Required (`@NotBlank`)
  * `category`: Required (`@NotNull`), Enum (`SPIRITUAL_MORAL`, `LIFESTYLE_PRODUCTIVITY`)
  * `subCategory`: Required (`@NotNull`), Enum (`PMO_RECOVERY`, `SMOKING_VAPING`, `DIGITAL_SCROLLING`, `GENERAL_HABIT`)
  * `targetDays`: Positive integer (`@Min(1)`)
  * `privacyLevel`: Required (`@NotNull`), Enum (`LEVEL_0_PRIVATE`, `LEVEL_1_STREAK_ONLY`, `LEVEL_2_FULL_COUNSEL`)
* **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "message": "Habit chain created successfully",
    "data": {
      "id": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
      "userId": "c0a80069-9fb9-1430-819f-b97453210000",
      "title": "PMO Freedom & Tazkiyah",
      "description": "Daily commitment to purity of mind and sight.",
      "category": "SPIRITUAL_MORAL",
      "subCategory": "PMO_RECOVERY",
      "currentStreak": 0,
      "longestStreak": 0,
      "totalCleanDays": 0,
      "startDate": "2026-08-02",
      "lastCheckInDate": null,
      "targetDays": 90,
      "privacyLevel": "LEVEL_2_FULL_COUNSEL",
      "customPledge": "I pledge to lower my gaze and seek refuge whenever triggered.",
      "status": "ACTIVE",
      "createdAt": "2026-08-02T09:00:00",
      "updatedAt": "2026-08-02T09:00:00"
    }
  }
  ```

#### 2.4.2 Get User Habit Chains
Lists habit chains owned by the authenticated user.

* **Method:** `GET`
* **Path:** `/api/v1/chains`
* **Authentication:** `Bearer <accessToken>`
* **Query Parameters:**
  * `status` (Optional): Filter by `ChainStatus` (`ACTIVE`, `ARCHIVED`, `GRADUATED`)
* **Response `200 OK`:** Returns array of `HabitChainResponse` objects wrapped in `data`.

#### 2.4.3 Get Habit Chain by ID
* **Method:** `GET`
* **Path:** `/api/v1/chains/{id}`
* **Authentication:** `Bearer <accessToken>`
* **Path Variable:** `id` (UUID)
* **Response `200 OK`:** Returns `HabitChainResponse` object.
* **Error Scenarios:** `404 NOT_FOUND`

#### 2.4.4 Update Habit Chain
Updates chain configuration, target days, privacy level, or status.

* **Method:** `PUT`
* **Path:** `/api/v1/chains/{id}`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "title": "PMO Freedom (Updated)",
    "description": "Refocused plan for 90 days clean.",
    "targetDays": 90,
    "privacyLevel": "LEVEL_2_FULL_COUNSEL",
    "customPledge": "I commit to daily dhikr and morning routines.",
    "status": "ACTIVE"
  }
  ```
* **Response `200 OK`:** Returns updated `HabitChainResponse`.

#### 2.4.5 Delete Habit Chain
Deletes a habit chain and cascades deletion to associated logs and invites.

* **Method:** `DELETE`
* **Path:** `/api/v1/chains/{id}`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Habit chain deleted successfully",
    "data": null
  }
  ```

---

### 2.5 Check-In Logs & Streak Resilience (`/api/v1/chains/{id}/logs`)

#### 2.5.1 Submit Check-In Log
Logs daily check-in status (`CLEAN`, `URGE_RESISTED`, `PEEKED_EDGED`, `SLIP_UP`). If status is `SLIP_UP`, automated post-slip guidance is generated.

* **Method:** `POST`
* **Path:** `/api/v1/chains/{id}/logs`
* **Authentication:** `Bearer <accessToken>`
* **Path Variable:** `id` (UUID - Chain ID)
* **Request Body:**
  ```json
  {
    "status": "SLIP_UP",
    "notes": "Felt overwhelmed late night after late screen usage.",
    "triggerReason": "Late night tiredness + phone in bed",
    "mood": "Regretful but committed to reset"
  }
  ```
* **Validation Rules:**
  * `status`: Required (`@NotNull`), Enum (`CLEAN`, `URGE_RESISTED`, `PEEKED_EDGED`, `SLIP_UP`)
* **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "message": "Check-in logged successfully",
    "data": {
      "id": "b3c4d5e6-f7a8-9012-bcde-34567890abcd",
      "chainId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
      "checkInDate": "2026-08-02",
      "status": "SLIP_UP",
      "notes": "Felt overwhelmed late night after late screen usage.",
      "triggerReason": "Late night tiredness + phone in bed",
      "mood": "Regretful but committed to reset",
      "streakAfterCheckIn": 0,
      "guidance": {
        "spiritualCounsel": "Do not despair of Allah's mercy. Perform Ghusl/Wudu and 2 Rak'ahs of Tawbah immediately.",
        "psychologicalAction": "Write down the trigger sequence and leave your phone outside the bedroom tonight.",
        "suggestedDua": "Rabbi inni zalamtu nafsi faghfir li"
      }
    }
  }
  ```

#### 2.5.2 Get Check-In History
Retrieves full log history for a habit chain.

* **Method:** `GET`
* **Path:** `/api/v1/chains/{id}/logs`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:** Returns array of `CheckInResponse` objects.

#### 2.5.3 Delete Check-In Log
* **Method:** `DELETE`
* **Path:** `/api/v1/chains/{id}/logs/{logId}`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:** Returns success response with message.

---

### 2.6 Emergency SOS & Urge Surfing (`/api/v1/emergency`)

#### 2.6.1 Start Emergency SOS Session
Initiates an urge-surfing intervention session with immediate dhikr, breathing exercises, and reflection.

* **Method:** `POST`
* **Path:** `/api/v1/emergency/start`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "emergencyType": "SPIRITUAL",
    "currentUrgeIntensity": 8
  }
  ```
* **Validation Rules:**
  * `emergencyType`: Required (`@NotNull`), Enum (`SPIRITUAL`, `PSYCHOLOGICAL`, `PHYSICAL_CIRCUIT_BREAKER`)
  * `currentUrgeIntensity`: Required (`@Min(1)`, `@Max(10)`)
* **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "message": "Emergency SOS session started",
    "data": {
      "sessionId": "d4e5f6a7-b8c9-0123-defa-45678901abcd",
      "emergencyType": "SPIRITUAL",
      "title": "Spiritual Circuit Breaker & Refuge",
      "dhikrOrAffirmation": "Ya Muqallibal-qulub, thabbit qalbi 'ala dinik.",
      "breathingExerciseDurationSeconds": 180,
      "reflectiveQuestions": [
        "What emotion triggered this urge right now?",
        "Will 5 minutes of pleasure be worth resetting your streak?"
      ],
      "emergencyContactPrompt": "Reach out to your accountability partner immediately if urge persists."
    }
  }
  ```

#### 2.6.2 Complete Emergency SOS Session
Finalizes an active SOS session, recording whether the urge was successfully survived.

* **Method:** `POST`
* **Path:** `/api/v1/emergency/{sessionId}/complete`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "postUrgeIntensity": 2,
    "userNotes": "Deep breathing and cold water reset worked.",
    "survived": true
  }
  ```
* **Response `200 OK`:** Returns `EmergencySessionResponse`.

#### 2.6.3 Get Emergency SOS History
* **Method:** `GET`
* **Path:** `/api/v1/emergency/history`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:** Returns array of `EmergencySessionResponse` objects.

---

### 2.7 Mentorship, Accountability & Counseling (`/api/v1`)

#### 2.7.1 Generate Partner Invite Code
Generates a unique 6-character invitation code to connect an accountability partner or mentor to a chain.

* **Method:** `POST`
* **Path:** `/api/v1/chains/{id}/partners/invite`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "role": "SPIRITUAL_MENTOR",
    "expirationDays": 7
  }
  ```
* **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "message": "Partner invite code generated successfully",
    "data": {
      "inviteCode": "BC-8X92KP",
      "chainId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
      "expiresAt": "2026-08-09T09:00:00",
      "role": "SPIRITUAL_MENTOR"
    }
  }
  ```

#### 2.7.2 Accept Partner Invite Code
* **Method:** `POST`
* **Path:** `/api/v1/partners/accept`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "inviteCode": "BC-8X92KP"
  }
  ```
* **Response `200 OK`:** Returns `InvitePartnerResponse`.

#### 2.7.3 Get Mentees List (For Mentors)
Retrieves all habit chains where the authenticated user is an accepted mentor or accountability partner.

* **Method:** `GET`
* **Path:** `/api/v1/partners/mentees`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:** Returns array of `HabitChainResponse` objects.

#### 2.7.4 Submit Mentor Counsel Note (*Nasiha*)
Allows an accepted mentor to leave counsel notes on a mentee's habit chain.

* **Method:** `POST`
* **Path:** `/api/v1/chains/{id}/counsel-notes`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "counselText": "Stay steadfast during weekend idle times. Increase evening Quran recitation.",
    "isPrivate": false
  }
  ```
* **Response `201 Created`:** Returns `CounselNoteResponse`.

#### 2.7.5 Get Chain Counsel Notes
* **Method:** `GET`
* **Path:** `/api/v1/chains/{id}/counsel-notes`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:** Returns array of `CounselNoteResponse` objects.

#### 2.7.6 Send 2-Way Mentorship Chat Message
* **Method:** `POST`
* **Path:** `/api/v1/partnerships/{partnershipId}/messages`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "messageContent": "Assalamu Alaikum, I completed my 7-day clean streak today and feel much stronger!"
  }
  ```
* **Response `201 Created`:**
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

#### 2.7.7 Get 2-Way Mentorship Chat History
* **Method:** `GET`
* **Path:** `/api/v1/partnerships/{partnershipId}/messages`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:** Returns array of `PartnerMessageResponse` objects.

#### 2.7.8 Send Partner Distress SOS Alert
Dispatches an instant distress notification to the designated partner for a habit chain.

* **Method:** `POST`
* **Path:** `/api/v1/partners/distress-alert`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "chainId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
    "message": "Urgent: High trigger warning late night. Need counsel."
  }
  ```
* **Validation Rules:**
  * `chainId`: Required (`@NotNull`)
* **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Partner distress alert sent successfully",
    "data": null
  }
  ```

---

### 2.8 Analytics, Milestones & Barakah (`/api/v1`)

#### 2.8.1 Get Chain Analytics
Calculates clean percentage, streak statistics, common triggers, and historical breakdown.

* **Method:** `GET`
* **Path:** `/api/v1/chains/{id}/analytics`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": {
      "chainId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
      "currentStreak": 14,
      "longestStreak": 21,
      "totalCleanDays": 45,
      "cleanPercentage": 91.8,
      "totalCheckIns": 49,
      "cleanCount": 42,
      "urgeResistedCount": 3,
      "peekedEdgedCount": 2,
      "slipUpCount": 2,
      "commonTriggers": [
        "Late night phone usage",
        "Boredom / Stress"
      ],
      "streakHistoryDays": 49
    }
  }
  ```

#### 2.8.2 Get User Milestone Badges
Retrieves all milestone achievements (Day 3, 7, 21, 40, 90) and earned status for the user.

* **Method:** `GET`
* **Path:** `/api/v1/milestones`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "data": [
      {
        "badgeType": "NAFS_AMMARAH_SURVIVOR",
        "badgeName": "Nafs Ammarah Survivor",
        "description": "Survived the peak physical withdrawal period of Day 3.",
        "dayThreshold": 3,
        "earned": true,
        "earnedAt": "2026-07-20T10:00:00",
        "iconUrl": "https://assets.breakingchains.com/badges/day3.png"
      },
      {
        "badgeType": "NAFS_LAWWAMAH_STRIVER",
        "badgeName": "Nafs Lawwamah Striver",
        "description": "Completed Day 7 Dopamine Reset Foundation.",
        "dayThreshold": 7,
        "earned": true,
        "earnedAt": "2026-07-24T10:00:00",
        "iconUrl": "https://assets.breakingchains.com/badges/day7.png"
      }
    ]
  }
  ```

---

### 2.9 Verified Mentor Directory & Verification (`/api/v1/mentors`)

#### 2.9.1 Register as a Mentor
Submits a registration application to become a verified mentor/counselor.

* **Method:** `POST`
* **Path:** `/api/v1/mentors/register`
* **Authentication:** `Bearer <accessToken>`
* **Request Body:**
  ```json
  {
    "qualification": "Alimiyyah Degree in Islamic Studies",
    "specialization": "Spiritual Counsel (Tazkiyah)",
    "yearsOfExperience": 5,
    "organization": "Al-Hikmah Youth Center",
    "bio": "Experienced spiritual guide specializing in heart purity and recovery support.",
    "autoApprove": false
  }
  ```
* **Validation Rules:**
  * `qualification`: Required (`@NotBlank`)
  * `specialization`: Required (`@NotBlank`)
  * `yearsOfExperience`: Required (`@NotNull`, `@Min(0)`)
* **Response `201 Created`:**
  ```json
  {
    "status": "success",
    "message": "Mentor registration application submitted successfully",
    "data": {
      "id": "e4f3d2c1-b0a9-8765-fedc-4321098765ba",
      "userId": "c0a80069-9fb9-1430-819f-b97453210000",
      "fullName": "Sheikh Ahmad",
      "username": "sheikh_ahmad",
      "avatarUrl": null,
      "qualification": "Alimiyyah Degree in Islamic Studies",
      "specialization": "Spiritual Counsel (Tazkiyah)",
      "yearsOfExperience": 5,
      "organization": "Al-Hikmah Youth Center",
      "bio": "Experienced spiritual guide specializing in heart purity and recovery support.",
      "status": "PENDING",
      "isVerified": false,
      "createdAt": "2026-08-01T17:50:00"
    }
  }
  ```

#### 2.9.2 Get My Mentor Profile
* **Method:** `GET`
* **Path:** `/api/v1/mentors/me`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:** Returns `MentorProfileResponse` wrapped in `ApiResponse`.

#### 2.9.3 Get Verified Mentors Directory
Retrieves all approved verified mentors (`status: APPROVED`).

* **Method:** `GET`
* **Path:** `/api/v1/mentors/verified`
* **Authentication:** `Public` / `Bearer Token`
* **Response `200 OK`:** Returns array of verified `MentorProfileResponse` objects wrapped in `ApiResponse`.

#### 2.9.4 Get All Mentor Applications (Admin / Dev)
* **Method:** `GET`
* **Path:** `/api/v1/mentors/applications`
* **Authentication:** `Bearer <accessToken>`
* **Response `200 OK`:** Returns array of all `MentorProfileResponse` objects wrapped in `ApiResponse`.

#### 2.9.5 Update Mentor Application Status (Admin / Dev)
Approves or rejects a mentor registration application. Approving sets `user.isVerifiedMentor = true`.

* **Method:** `PUT`
* **Path:** `/api/v1/mentors/applications/{profileId}/status`
* **Authentication:** `Bearer <accessToken>`
* **Path Variable:** `profileId` (UUID)
* **Request Body:**
  ```json
  {
    "status": "APPROVED"
  }
  ```
* **Validation Rules:**
  * `status`: Required (`@NotNull`), Enum (`APPROVED`, `REJECTED`, `PENDING`)
* **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "message": "Mentor application status updated successfully",
    "data": {
      "id": "e4f3d2c1-b0a9-8765-fedc-4321098765ba",
      "userId": "c0a80069-9fb9-1430-819f-b97453210000",
      "fullName": "Sheikh Ahmad",
      "username": "sheikh_ahmad",
      "avatarUrl": null,
      "qualification": "Alimiyyah Degree in Islamic Studies",
      "specialization": "Spiritual Counsel (Tazkiyah)",
      "yearsOfExperience": 5,
      "organization": "Al-Hikmah Youth Center",
      "bio": "Experienced spiritual guide specializing in heart purity and recovery support.",
      "status": "APPROVED",
      "isVerified": true,
      "createdAt": "2026-08-01T17:50:00"
    }
  }
  ```
