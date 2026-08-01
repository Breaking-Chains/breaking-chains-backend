# Breaking Chains — Master Project Specification & Architecture Blueprint

**Project Name:** Breaking Chains (`breaking-chains-backend`)  
**Core Purpose:** Empower individuals to break destructive habits, overcome addictions, and build self-discipline through a synthesis of **Islamic Spiritual Psychology (*Tazkiyah al-Nafs*)**, **Modern Behavioral Science**, and **High-Performance Engineering**.

---

## 1. 🌟 Product Vision & Core Philosophy

Breaking a habit requires more than raw willpower. It requires **spiritual alignment, psychological reframing, and structured behavioral discipline**.

### 1.1 The Dual Classification Engine: Sin vs. Non-Sin Bad Habits

A core innovation of Breaking Chains is recognizing that **not all bad habits are sins**, and treating them all as religious transgressions leads to unnecessary guilt or spiritual burnout. Conversely, treating spiritual transgressions as mere habit loops ignores the soul's need for repentance and divine connection.

```
                               ┌───────────────────────────────────┐
                               │       HABIT CHAIN CREATION        │
                               └─────────────────┬─────────────────┘
                                                 │
                        ┌────────────────────────┴────────────────────────┐
                        │                                                 │
                        ▼                                                 ▼
     ┌────────────────────────────────────┐            ┌────────────────────────────────────┐
     │ CATEGORY A: SPIRITUAL / MORAL      │            │ CATEGORY B: LIFESTYLE / FUNCTIONAL │
     │ (Forbidden Acts / Sins)            │            │ (Non-Sin Bad Habits)               │
     │ e.g. Addictions, PMO, Backbiting  │            │ e.g. Junk Food, Procrastination,   │
     │      Lying, Gambling, Alcohol      │            │      Excessive Scrolling, Nails   │
     └─────────────────┬──────────────────┘            └─────────────────┬──────────────────┘
                       │                                                 │
                       ▼                                                 ▼
     ┌────────────────────────────────────┐            ┌────────────────────────────────────┐
     │ SPIRITUAL PURIFICATION ENGINE      │            │ BEHAVIORAL EFFICIENCY ENGINE       │
     │ • Tawbah & Istighfar (Repentance)  │            │ • Habit Loop (Cue-Craving-Reward)  │
     │ • Good Deed Replacement (Hasanat)  │            │ • Friction Tuning & Replacements   │
     │ • Spiritual Hope (Ar-Raja')        │            │ • Time/Energy Barakah Tracking     │
     │ • Nafs Transformation Stages       │            │ • Productivity Metric Optimization  │
     │ • Optional Spiritual Guide/Counsel │            │ • Optional Peer Accountability     │
     └────────────────────────────────────┘            └────────────────────────────────────┘
```

#### Category A: Spiritual & Moral Chains (Sins & Transgressions)
* **Goal:** Spiritual purification (*Tazkiyah*), breaking spiritual shackles, restoring relationship with Allah.
* **Core Principles:**
  * **Tawbah (Repentance) as a Dynamic Reset:** A slip-up is met with immediate *Tawbah*, *Istighfar*, and performing a good deed (*Al-Hasanat yudhibna al-sayyi'at* — "Good deeds erase bad deeds").
  * **Hope over Despair:** Eliminate destructive guilt spirals. Focus on Allah's vast mercy (*Rahmah*).
  * **Sadaqah Penalty / Redemption:** Option to convert money saved from a bad habit into charity (*Sadaqah*).
  * **Optional Confidential Counsel (*Suhbah*):** Access to a trusted spiritual guide or mentor while maintaining strict personal privacy.

#### Category B: Functional & Lifestyle Chains (Non-Sin Bad Habits)
* **Goal:** Self-mastery, focus, time stewardship (*Barakah* in time), physical health, routine optimization.
* **Core Principles:**
  * **Habit Loop Optimization:** Reducing friction for good behaviors, increasing friction for bad ones.
  * **No Religious Guilt:** Clean, practical, performance-oriented tracking.
  * **Energy & Focus Metrics:** Tracking hours saved, mental clarity, and habit replacement success.

---

## 2. 🧠 Spiritual & Behavioral Science Framework

### 2.1 The 3 Stages of the Soul (*Nafs*) as Progression Markers

Instead of generic levels, user milestone progression is modeled after the 3 Quranic states of the human soul:

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│ STAGE 1: Nafs al-Ammarah (The Inclining Soul) [Days 1 – 7]                       │
│ High vulnerability, acute withdrawal, intense cravings. Focus on survival,       │
│ high-friction boundaries, and emergency intervention.                            │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│ STAGE 2: Nafs al-Lawwamah (The Self-Aware / Striving Soul) [Days 8 – 40]          │
│ Developing awareness, resisting urges, active struggle (Jihad al-Nafs). Focus   │
│ on trigger identification, consistency, and building substitute routines.        │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│ STAGE 3: Nafs al-Mutma'innah (The Tranquil / Reclaimed Soul) [Days 40 – 90+]     │
│ Dopamine baseline reset, spiritual peace, deep habit consolidation. Focus on      │
│ stewardship, helping others, and long-term identity shift.                       │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 The Resilience Index (Beyond Simple Zero-Reset Streaks)

Standard apps reset a user's counter to `0` upon a single slip, causing the **Abstinence Violation Effect (AVE)** ("I lost my 50-day streak, I give up").

Breaking Chains calculates **Resilience Score & Clean Ratio**:
$$\text{Clean Ratio (\%)} = \left( \frac{\text{Total Clean Days}}{\text{Total Days Since Chain Start}} \right) \times 100$$

---

## 3. 📱 Core Product Modules & Feature Specifications

### Module 1: Chain Setup & Intent (*Niyyah*)
- Select Habit Category (`SPIRITUAL_MORAL` vs `LIFESTYLE_PRODUCTIVITY`).
- Name, Trigger Mapping (e.g. *Late night phone use*, *Stress after work*, *Boredom*).
- Financial & Time Cost Estimator.
- Substitute Action Pairings.
- Intent Statement (*Niyyah* commitment).

### Module 2: Low-Friction Daily Check-In & *Muhasabah* (Self-Reflection)
- 5-second check-in flow (`CLEAN`, `URGE_RESISTED`, `SLIP_UP`).
- Contextual tags (Mood, Location, Time of Day, Triggers).
- **If Slip-Up (Category A - Spiritual):** Instant *Istighfar* prompt + Good Deed Action suggestion.
- **If Slip-Up (Category B - Lifestyle):** Root cause tag + immediate substitute habit re-commitment.

### Module 3: Emergency "Break the Loop" (SOS Toolkit)
- **Spiritual SOS Stream:** Verses of Hope (*Ayat al-Raja'*), authentic *Du'as*, 2-Minute Wudu & Dhikr Counter. Option to trigger "Notify My Guide / Mentor" SOS alert.
- **Psychological SOS Stream:** 60-Second Box Breathing animation, 5-4-3-2-1 Grounding exercise, distraction puzzle.

### Module 4: Accountability, Guidance & Spiritual Counsel (*Suhbah & Counsel Engine*) 🤝
- **Why It Matters:** Research shows having a trusted accountability partner increases habit success rates from 65% to **95%**. In Islamic tradition, *Suhbah* (wholesome companionship) and *Nasiha* (sincere counsel) are foundational for *Tazkiyah*.
- **Privacy & Dignity First (*Satr*):** Islam mandates concealing one's faults/sins (*Satr*). Therefore, mentor sharing is **100% Optional**, **End-to-End User Controlled**, and supports **Granular Visibility Levels**:
  - `LEVEL_0_STRICTLY_PRIVATE` (Default): Fully hidden. Only user and Allah know.
  - `LEVEL_1_STREAK_ONLY`: Guide sees only high-level clean percentage & milestone badges.
  - `LEVEL_2_FULL_COUNSEL`: Guide can view check-in status and send encouraging text/voice notes (*Nasiha*).
- **Feature Capabilities:**
  - **Invite Link / Access Code:** User sends an encrypted invite code to a chosen mentor/counselor/friend.
  - **Counsel Notes (*Nasiha* Feed):** The guide can leave uplifting notes, Quranic reminders, or voice encouragements.
  - **Distress Signal ("Nudge My Guide"):** During an intense craving, 1-tap notifies the guide to make Du'a or send an immediate message.

### Module 5: Analytics & *Barakah* / Financial Impact Tracker
- **Financial Savings & Sadaqah Redemption:** Convert bad spending into charity.
- **Time Reclaimed (*Barakah*):** Hours saved converted into Quran reading or skill learning equivalent.
- **Trigger Pattern Radar:** Insights showing top trigger times and locations.

---

## 4. 🛠️ Backend System Architecture & Database Schema

### 4.1 Relational Data Model (PostgreSQL ERD)

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                       USERS                                            │
├────────────────────────────────────────────────────────────────────────────────────────┤
│ id (UUID, PK) | email | password_hash | full_name | username | auth_provider            │
└──────────────────────────────────────────┬─────────────────────────────────────────────┘
                                           │ 1
                                           │
                                           │ *
┌──────────────────────────────────────────┴─────────────────────────────────────────────┐
│                                    HABIT_CHAINS                                        │
├────────────────────────────────────────────────────────────────────────────────────────┤
│ id (UUID, PK) | user_id (UUID, FK) | title | category (SPIRITUAL_MORAL / LIFESTYLE)    │
│ privacy_level (LEVEL_0_PRIVATE, LEVEL_1_STREAK_ONLY, LEVEL_2_FULL_COUNSEL)             │
└──────────────────────────────────────────┬─────────────────────────────────────────────┘
                                           │
            ┌──────────────────────────────┼──────────────────────────────┐
            │ 1                            │ 1                            │ 1
            │                              │                              │
            │ *                            │ *                            │ *
┌───────────┴─────────────────┐  ┌─────────┴───────────────────┐  ┌───────┴──────────────────────┐
│         LOG_ENTRIES         │  │    ACCOUNTABILITY_PARTNERS  │  │      COUNSEL_NOTES          │
├─────────────────────────────┤  ├────────────────────────────┤  ├─────────────────────────────┤
│ id (UUID, PK)               │  │ id (UUID, PK)              │  │ id (UUID, PK)               │
│ chain_id (UUID, FK)         │  │ chain_id (UUID, FK)        │  │ chain_id (UUID, FK)         │
│ user_id (UUID, FK)          │  │ user_id (UUID, FK)         │  │ mentor_user_id (UUID, FK)   │
│ log_timestamp (TIMESTAMP)   │  │ partner_user_id (UUID, FK) │  │ note_content (TEXT)         │
│ status (CLEAN, SLIP, RESIST)│  │ role (MENTOR, PEER_BUDDY)  │  │ created_at (TIMESTAMP)      │
└─────────────────────────────┘  │ status (PENDING, ACCEPTED) │  └─────────────────────────────┘
                                 └────────────────────────────┘
```

---

## 5. 📡 Complete API Contract Specification

### 5.1 Habit & Log Endpoints (Completed & Specified ✅)
- `POST /api/v1/chains`, `GET /api/v1/chains`, `POST /api/v1/chains/{id}/logs`, `POST /api/v1/emergency/start`

### 5.2 Accountability & Guidance Endpoints (`/api/v1/partners`)
- `POST /api/v1/chains/{id}/partners/invite` — Generate an encrypted invite code for a mentor/buddy.
- `POST /api/v1/partners/accept` — Accept invitation to guide/track a user's chain.
- `GET /api/v1/partners/mentees` — Retrieve list of users a mentor is currently guiding.
- `POST /api/v1/chains/{id}/counsel-notes` — Mentor leaves a counsel note (*Nasiha*) for a user.
- `GET /api/v1/chains/{id}/counsel-notes` — Retrieve counsel notes for a chain.
- `POST /api/v1/emergency/notify-partner` — Trigger immediate distress ping to guide.

---

## 6. 🗺️ Implementation Execution Roadmap

```
Phase 1: Core Habit Chain Domain (HabitChain CRUD + Base Data Models)
Phase 2: Check-In & Resilience Engine (LogEntry API + Clean Ratio & Category Responses)
Phase 3: Emergency "Break the Loop" Toolkit (SOS Content Stream & Distractions)
Phase 4: Accountability, Guidance & Counsel Engine (Partner Invites, Privacy Levels & Counsel Notes)
Phase 5: Analytics, Milestones & Barakah Savings Engine
```
