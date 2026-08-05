# Seeding Guide - Breaking Chains Database (PMO Recovery Focus)

This guide explains how to run the `seed.sql` script to populate your database with rich, representative test data centered exclusively on **PMO Recovery (Pornography / Masturbation / Orgasm)**, covering every entity, relationship, and business rule constraint.

---

## 🚀 Seeding the Database

Ensure the PostgreSQL database container is running. If not, start it:
```bash
docker compose up postgres -d
```

### Option A: Via Docker Command (Recommended for local)
From your terminal in the project root folder, execute:
```bash
docker exec -i breaking_chains_postgres psql -U postgres -d breaking_chains_db < seed.sql
```

### Option B: Via Local PostgreSQL Client (`psql`)
If you have PostgreSQL client tools installed locally, execute:
```bash
psql -h localhost -p 5433 -U postgres -d breaking_chains_db -f seed.sql
```
*(Password is `password` when prompted).*

### Option C: Cloud Database / Neon Seeding
If you are seeding a cloud database like **Neon**, use one of the following methods:

1. **Via `psql` Terminal (Easiest command line option):**
   Copy your Neon connection URI (with password) from the Neon Console dashboard and run:
   ```bash
   psql "postgresql://[user]:[password]@[neon-host]/[db_name]?sslmode=require" -f seed.sql
   ```

2. **Via Neon Web SQL Editor:**
   - Go to your [Neon Console](https://console.neon.tech).
   - Select your project, branch, and database.
   - Go to the **SQL Editor** menu in the sidebar.
   - Copy the entire contents of [seed.sql](file:///p:/WORKSPACE/KBLabs/breaking-chains-backend/seed.sql), paste it into the editor, and click **Run**.

3. **Via Database IDE (DBeaver / pgAdmin / IntelliJ Database Tool)**
   - Create a connection using host, database, user, and password details from the Neon console.
   - **Crucial:** Ensure **SSL mode** is set to `require` or `verify-full` in connection settings.
   - Run the script within your SQL worksheet.

---

## 👥 Seeded User Profiles & Credentials

All local accounts are seeded with the password: **`Password123!`**  
The JWT tokens and security mechanisms are fully compatible.

| Role / Profile | Name | Email / Credentials | Purpose / Data State |
| :--- | :--- | :--- | :--- |
| **Standard User** | Alex Smith | `alex.smith@example.com` | Struggles with PMO. Has active chain `PMO Freedom & Tazkiyah` (`LEVEL_2_FULL_COUNSEL` privacy) with check-in logs, a slip-up, active chaser protection alert, earned milestones, and active chats. Also has a `GRADUATED` `30-Day Purity Kickstart` chain. |
| **Standard User** | Sarah Jones | `sarah.jones@example.com` | Struggles with PMO. Has active chain `Purity & Mindfulness` (`LEVEL_1_STREAK_ONLY` privacy) with a clean streak, and an `ARCHIVED` `PMO Reset Attempt` chain. |
| **Approved Mentor**| Sheikh Ahmad Al-Taji | `sheikh.ahmad@breakingchains.com` | Approved verified mentor profile specializing in Tazkiyah & PMO Recovery. Connected to Alex Smith's chain. Has left counsel notes (*Nasiha*) and sent encouraging chat messages. |
| **Accountability Peer**| John Doe | `partner.john@example.com` | Peer buddy in PMO recovery. Has active chain `Steadfast Purity` (15 days clean). Buddy to both Alex Smith and Sarah Jones. |
| **Pending Mentor** | Sheikh Hamza Yusuf | `sheikh.hamza@breakingchains.com` | Registered as a mentor (PMO specialization) but status is `PENDING`. Can be approved by the admin. |
| **System Admin** | System Administrator | `admin@breakingchains.com` | Admin role to test administrative operations like approving mentor applications. |
| **Google User** | Bilal Khan | `bilal.khan@gmail.com` (Google Auth) | Google OAuth user seeking tazkiyah from PMO. Google ID: `12345678901234567890` (Password is null, test via `/api/v1/auth/google`). |

---

## 📊 Seeded Data Scenarios & Validation Checklists

### Scenario 1: The Purity Recovery Journey (Alex & Sheikh Ahmad)
- **Entities Covered:** `User`, `HabitChain`, `LogEntry`, `EmergencySession`, `AccountabilityPartner`, `CounselNote`, `PartnerMessage`, `MilestoneBadge`.
- **Habit Chain ID:** `c0000000-0000-0000-0000-000000000001`
- **What to Validate:**
  - Login as Alex, retrieve his active chains (`GET /api/v1/chains`).
  - Retrieve check-in logs (`GET /api/v1/chains/c0000000-0000-0000-0000-000000000001/logs`).
    - Note the slip-up on Day 8 due to `"Boredom / loneliness"`. Verify the response includes the **automated post-slip guidance** (wudu steps, Tawbah prayer guide, Sadaqah penalty) and activates `chaser_alert_active = true`.
  - Retrieve analytics (`GET /api/v1/chains/c0000000-0000-0000-0000-000000000001/analytics`). Verify clean ratio and streak metrics.
  - Check milestones (`GET /api/v1/milestones`). Verify the `NAFS_AMMARAH_SURVIVOR` (Day 3) badge is unlocked.
  - Review counsel notes (`GET /api/v1/chains/c0000000-0000-0000-0000-000000000001/counsel-notes`). Verify the two *Nasiha* entries left by Sheikh Ahmad.
  - Get Chat history with mentor (`GET /api/v1/partnerships/d0000000-0000-0000-0000-000000000001/messages`). Verify the 2-way conversation about urge-surfing wudu steps.

### Scenario 2: The Striving Soul & Streak Milestones (Sarah & John)
- **Entities Covered:** `User`, `HabitChain`, `LogEntry`, `AccountabilityPartner`, `MilestoneBadge`.
- **Habit Chain ID:** `c0000000-0000-0000-0000-000000000002`
- **What to Validate:**
  - Retrieve Sarah's active chains.
  - Verify that Sarah's clean streak is **8 days**.
  - Verify Sarah has unlocked both `NAFS_AMMARAH_SURVIVOR` (Day 3) and `NAFS_LAWWAMAH_STRIVER` (Day 7) badges.
  - Login as Partner John (`partner.john@example.com`) and retrieve mentees (`GET /api/v1/partners/mentees`). Sarah's chain should be visible to him since privacy is `LEVEL_1_STREAK_ONLY` and he is an accepted buddy.

### Scenario 3: Admin Review & Approving Mentors
- **Entities Covered:** `User`, `MentorProfile`.
- **What to Validate:**
  - Login as Sheikh Hamza (`sheikh.hamza@breakingchains.com`). Retrieve profile info and verify `isVerifiedMentor = false`. Retrieve mentor profile (`GET /api/v1/mentors/me`) to see status is `PENDING`.
  - Login as Admin (`admin@breakingchains.com`), retrieve applications (`GET /api/v1/mentors/applications`).
  - Approve Sheikh Hamza (`PUT /api/v1/mentors/applications/e0000000-0000-0000-0000-000000000002/status` with body `{"status": "APPROVED"}`).
  - Verify Sheikh Hamza's user record now updates to `isVerifiedMentor = true`.
