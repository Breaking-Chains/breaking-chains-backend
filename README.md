# Breaking Chains - Backend Service

TypeScript & Node.js REST API service for **Breaking Chains** Android application. Provides authentication (Email/Password & Google Sign-In), token rotation, and profile management.

---

## Technical Stack

- **Runtime & Language:** Node.js, TypeScript (ES2022)
- **Web Framework:** Express.js v5
- **Database & ORM:** PostgreSQL + Prisma ORM v6
- **Authentication:** JWT (Access + Refresh token rotation), `bcryptjs`, Google Identity (`google-auth-library`)
- **Validation:** Zod schema validation
- **Testing:** Jest & Supertest

---

## Project Structure

```text
breaking-chains-backend/
├── API_CONTRACT.md          # Standalone API contract for Frontend/Android developers
├── prisma/
│   └── schema.prisma        # Prisma PostgreSQL schema (User, RefreshToken)
├── src/
│   ├── config/              # Environment config & Prisma client singleton
│   ├── errors/              # AppError custom exception handling
│   ├── middlewares/         # Auth, validation & global error middlewares
│   ├── modules/
│   │   ├── auth/            # Auth controller, service, schema & routes
│   │   └── user/            # User profile controller, service, schema & routes
│   ├── utils/               # JWT sign & verify helpers
│   ├── app.ts               # Express application initialization
│   └── server.ts            # Server entry point
├── tests/                   # Jest unit & integration test suites
├── .env.example             # Environment variables template
├── package.json
└── tsconfig.json
```

---

## Setup & Local Development

### 1. Prerequisites
- Node.js (v18+ recommended)
- PostgreSQL database running locally or remotely

### 2. Environment Configuration
Copy `.env.example` to `.env` and set your credentials:

```bash
cp .env.example .env
```

Update `DATABASE_URL` in `.env`:
```env
DATABASE_URL="postgresql://postgres:password@localhost:5432/breaking_chains_db?schema=public"
```

### 3. Database Migration
Generate Prisma client and apply database migrations:

```bash
# Generate Prisma Client
npm run prisma:generate

# Run Database Migrations
npm run prisma:migrate
```

### 4. Running Dev Server
Start the development server with live reload:

```bash
npm run dev
```

Server will run on `http://localhost:5000`. Test healthcheck at `http://localhost:5000/health`.

---

## Available Scripts

- `npm run dev`: Start dev server with `ts-node-dev` (hot reload).
- `npm run build`: Compile TypeScript code to `/dist`.
- `npm start`: Run compiled production code from `/dist/server.js`.
- `npm test`: Run Jest unit and integration tests.
- `npm run prisma:generate`: Re-generate Prisma Client.
- `npm run prisma:migrate`: Create and execute database migration scripts.

---

## API Contract Specification

See [`API_CONTRACT.md`](./API_CONTRACT.md) for full endpoints documentation, request/response formats, TypeScript interfaces, and error codes.
