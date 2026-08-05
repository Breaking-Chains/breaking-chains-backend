-- =============================================================================
-- Breaking Chains - PMO-Focused Database Seed Script
-- =============================================================================
-- This script populates the PostgreSQL database with representative test data
-- centered exclusively on PMO Recovery (Pornography / Masturbation / Orgasm).
-- All passwords for local authenticated users are set to: Password123!
-- BCrypt Hash: $2a$10$HLJF1FS/AXv0eL8gSBdjReJwzBvrOQXSbS5amRKLA72MDk1dij8a2
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 0. Clean Existing Data (Reverse order of dependencies)
-- -----------------------------------------------------------------------------
TRUNCATE TABLE 
    partner_messages,
    counsel_notes,
    milestone_badges,
    emergency_sessions,
    log_entries,
    habit_chain_triggers,
    accountability_partnerships,
    habit_chains,
    mentor_profiles,
    refresh_tokens,
    users
    CASCADE;

-- -----------------------------------------------------------------------------
-- 1. Users Table Seeding
-- -----------------------------------------------------------------------------
-- Predefined UUIDs are used for test user referencing and ease of API testing
INSERT INTO users (
    id, email, password, full_name, username, avatar_url, bio, auth_provider, google_id, is_verified_mentor, role, created_at, updated_at
) VALUES 
-- 1. Admin User
(
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 
    'admin@breakingchains.com', 
    '$2a$10$HLJF1FS/AXv0eL8gSBdjReJwzBvrOQXSbS5amRKLA72MDk1dij8a2', 
    'System Administrator', 
    'admin_user', 
    'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&h=150', 
    'Breaking Chains system administrator.', 
    'LOCAL', 
    NULL, 
    FALSE, 
    'ADMIN', 
    NOW() - INTERVAL '15 days', 
    NOW() - INTERVAL '15 days'
),
-- 2. Sheikh Ahmad (Approved Mentor)
(
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 
    'sheikh.ahmad@breakingchains.com', 
    '$2a$10$HLJF1FS/AXv0eL8gSBdjReJwzBvrOQXSbS5amRKLA72MDk1dij8a2', 
    'Sheikh Ahmad Al-Taji', 
    'sheikh_ahmad', 
    'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=150&h=150', 
    'Alim and spiritual counselor specializing in Tazkiyah and youth PMO recovery support.', 
    'LOCAL', 
    NULL, 
    TRUE, 
    'USER', 
    NOW() - INTERVAL '12 days', 
    NOW() - INTERVAL '12 days'
),
-- 3. Sheikh Hamza (Pending Mentor Application)
(
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 
    'sheikh.hamza@breakingchains.com', 
    '$2a$10$HLJF1FS/AXv0eL8gSBdjReJwzBvrOQXSbS5amRKLA72MDk1dij8a2', 
    'Sheikh Hamza Yusuf', 
    'sheikh_hamza', 
    'https://images.unsplash.com/photo-1566492031773-4f4e44671857?auto=format&fit=crop&w=150&h=150', 
    'Student of knowledge and counselor striving to assist youth in porn and device addiction recovery.', 
    'LOCAL', 
    NULL, 
    FALSE, 
    'USER', 
    NOW() - INTERVAL '10 days', 
    NOW() - INTERVAL '10 days'
),
-- 4. Alex Smith (Standard User with PMO Chain and Graduated 30-day challenge)
(
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    'alex.smith@example.com', 
    '$2a$10$HLJF1FS/AXv0eL8gSBdjReJwzBvrOQXSbS5amRKLA72MDk1dij8a2', 
    'Alex Smith', 
    'alexsmith', 
    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&h=150', 
    'Striving daily to lower my gaze, purify my heart, and break free from PMO.', 
    'LOCAL', 
    NULL, 
    FALSE, 
    'USER', 
    NOW() - INTERVAL '30 days', 
    NOW() - INTERVAL '30 days'
),
-- 5. Sarah Jones (Standard User with active PMO chain and Archived PMO reset chain)
(
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 
    'sarah.jones@example.com', 
    '$2a$10$HLJF1FS/AXv0eL8gSBdjReJwzBvrOQXSbS5amRKLA72MDk1dij8a2', 
    'Sarah Jones', 
    'sarah_jones', 
    'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&h=150', 
    'Striving to reclaim my mental purity, time, and focus through PMO recovery.', 
    'LOCAL', 
    NULL, 
    FALSE, 
    'USER', 
    NOW() - INTERVAL '25 days', 
    NOW() - INTERVAL '25 days'
),
-- 6. Partner John (Accountability Partner / struggles with PMO)
(
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    'partner.john@example.com', 
    '$2a$10$HLJF1FS/AXv0eL8gSBdjReJwzBvrOQXSbS5amRKLA72MDk1dij8a2', 
    'John Doe', 
    'partner_john', 
    'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=150&h=150', 
    'Peer buddy in PMO recovery, helping friends build self-discipline and stay pure.', 
    'LOCAL', 
    NULL, 
    FALSE, 
    'USER', 
    NOW() - INTERVAL '20 days', 
    NOW() - INTERVAL '20 days'
),
-- 7. Bilal Khan (Google Auth User)
(
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a17', 
    'bilal.khan@gmail.com', 
    NULL, -- Google auth users do not have a password
    'Bilal Khan', 
    'bilal_khan', 
    'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=150&h=150', 
    'User authenticated via Google OAuth, seeking tazkiyah from PMO.', 
    'GOOGLE', 
    '12345678901234567890', 
    FALSE, 
    'USER', 
    NOW() - INTERVAL '5 days', 
    NOW() - INTERVAL '5 days'
);

-- -----------------------------------------------------------------------------
-- 2. Mentor Profiles Table Seeding
-- -----------------------------------------------------------------------------
INSERT INTO mentor_profiles (
    id, user_id, qualification, specialization, years_of_experience, organization, bio, status, invite_code, created_at, updated_at
) VALUES
-- Approved profile for Sheikh Ahmad
(
    'e0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Alimiyyah Degree in Islamic Studies & Counseling',
    'Spiritual Counsel (Tazkiyah) & Porn/Masturbation Addiction Recovery Support',
    8,
    'Al-Hikmah Youth Center & Guidance Clinic',
    'Experienced spiritual guide and certified counselor specializing in heart purity and recovery support.',
    'APPROVED',
    'MENTOR123',
    NOW() - INTERVAL '12 days',
    NOW() - INTERVAL '12 days'
),
-- Pending profile for Sheikh Hamza
(
    'e0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'MA in Counseling Psychology & Islamic Theology',
    'Youth Mental Health & PMO Habit Deconditioning',
    3,
    'Faith-Based Recovery Initiative',
    'Striving to help young Muslims navigate modern digital and behavioral challenges through traditional and clinical methods.',
    'PENDING',
    'MENTOR456',
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '10 days'
);

-- -----------------------------------------------------------------------------
-- 3. Habit Chains Table Seeding (All Centered on PMO Recovery)
-- -----------------------------------------------------------------------------
INSERT INTO habit_chains (
    id, user_id, title, description, category, sub_category, privacy_level, status, target_start_date, cost_per_instance, time_minutes_per_instance, substitute_action, intent_statement, created_at, updated_at
) VALUES
-- Chain 1: Alex - PMO Freedom & Tazkiyah (Spiritual/Moral - Active)
(
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'PMO Freedom & Tazkiyah',
    'Daily commitment to purity of mind and sight, rebuilding neural pathways and breaking PMO loops.',
    'SPIRITUAL_MORAL',
    'PMO_RECOVERY',
    'LEVEL_2_FULL_COUNSEL',
    'ACTIVE',
    CURRENT_DATE - 10, -- Started 10 days ago
    15.00,
    60,
    'Perform Wudu, pray 2 Rak''ahs of Salat al-Tawbah, and read Quran for 15 minutes.',
    'I pledge to lower my gaze, seek refuge with Allah, and build a pure heart free from PMO chains.',
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '10 days'
),
-- Chain 2: Sarah - PMO Recovery Journey (Spiritual/Moral - Active)
(
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Purity & Mindfulness (PMO Recovery)',
    'Breaking free from pornography addiction and healing my dopamine receptors.',
    'SPIRITUAL_MORAL',
    'PMO_RECOVERY',
    'LEVEL_1_STREAK_ONLY',
    'ACTIVE',
    CURRENT_DATE - 8, -- Started 8 days ago
    0.00,
    120,
    'Close electronic devices, do 15 minutes of grounding/reflection, or read a physical book.',
    'I commit to reclaiming my focus, self-worth, and spiritual purity under the supervision of my buddy.',
    NOW() - INTERVAL '8 days',
    NOW() - INTERVAL '8 days'
),
-- Chain 3: Partner John - Steadfast Purity (Spiritual/Moral - Active)
(
    'c0000000-0000-0000-0000-000000000003',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Steadfast Purity (PMO Recovery)',
    'Daily fight against triggers and objectification of others. Protecting eyes and thoughts.',
    'SPIRITUAL_MORAL',
    'PMO_RECOVERY',
    'LEVEL_0_PRIVATE',
    'ACTIVE',
    CURRENT_DATE - 15, -- Started 15 days ago
    0.00,
    30,
    'Drink cold water, call my accountability partner, or perform 10 quick pushups.',
    'I will protect the eyes and heart Allah gave me, seeking spiritual and physical cleanliness.',
    NOW() - INTERVAL '15 days',
    NOW() - INTERVAL '15 days'
),
-- Chain 4: Alex - 30-Day Purity Kickstart (Spiritual/Moral - Graduated!)
(
    'c0000000-0000-0000-0000-000000000004',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    '30-Day Purity Kickstart (PMO)',
    'Initial target of 30 days clean to reset basic neural pathways and habit triggers.',
    'SPIRITUAL_MORAL',
    'PMO_RECOVERY',
    'LEVEL_1_STREAK_ONLY',
    'GRADUATED',
    CURRENT_DATE - 30, -- Started 30 days ago
    10.00,
    60,
    'Do pushups, make wudu, or step out of the bedroom into a public space.',
    'I seek to break the first shackle of PMO and prove that with Allah''s help, I can stay clean.',
    NOW() - INTERVAL '30 days',
    NOW() - INTERVAL '9 days'
),
-- Chain 5: Sarah - PMO Reset Attempt (Spiritual/Moral - Archived)
(
    'c0000000-0000-0000-0000-000000000005',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'PMO Reset Attempt',
    'An unfocused purity attempt before establishing proper device restriction rules.',
    'SPIRITUAL_MORAL',
    'PMO_RECOVERY',
    'LEVEL_0_PRIVATE',
    'ARCHIVED',
    CURRENT_DATE - 25,
    0.00,
    60,
    'Call a friend or sit in the living room.',
    'Trying to stop PMO habit loops.',
    NOW() - INTERVAL '25 days',
    NOW() - INTERVAL '21 days'
);

-- -----------------------------------------------------------------------------
-- 4. Habit Chain Triggers Table Seeding (PMO Specific Triggers)
-- -----------------------------------------------------------------------------
INSERT INTO habit_chain_triggers (chain_id, trigger_tag) VALUES
-- Alex PMO Chain Triggers
('c0000000-0000-0000-0000-000000000001', 'Late night phone in bed'),
('c0000000-0000-0000-0000-000000000001', 'Boredom / loneliness'),
('c0000000-0000-0000-0000-000000000001', 'Social media explore page'),
-- Sarah PMO Chain Triggers
('c0000000-0000-0000-0000-000000000002', 'Boredom / isolation'),
('c0000000-0000-0000-0000-000000000002', 'Anxiety / stress'),
('c0000000-0000-0000-0000-000000000002', 'Triggering videos online'),
-- John PMO Chain Triggers
('c0000000-0000-0000-0000-000000000003', 'Late night device usage'),
('c0000000-0000-0000-0000-000000000003', 'Stress after work'),
('c0000000-0000-0000-0000-000000000003', 'Loneliness'),
-- Alex Graduated Kickstart Triggers
('c0000000-0000-0000-0000-000000000004', 'Boredom'),
('c0000000-0000-0000-0000-000000000004', 'Late night browser scrolling'),
-- Sarah Archived Reset Triggers
('c0000000-0000-0000-0000-000000000005', 'Late night phone in bed'),
('c0000000-0000-0000-0000-000000000005', 'Feeling overwhelmed');

-- -----------------------------------------------------------------------------
-- 5. Log Entries (Daily Check-Ins) Table Seeding (PMO Recovery Scenarios)
-- -----------------------------------------------------------------------------
INSERT INTO log_entries (
    id, chain_id, user_id, log_timestamp, status, intensity_level, trigger_tag, reflection_note, good_deed_done, chaser_alert_active, created_at
) VALUES
-- =============================================================================
-- LOGS FOR ALEX PMO CHAIN (c0000000-0000-0000-0000-000000000001)
-- =============================================================================
-- Day 1
(
    'b0000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 10) + TIME '21:00:00',
    'CLEAN',
    1,
    NULL,
    'First day clean. Niyyah set, feeling committed to staying away from PMO.',
    'Recited Morning Adhkar',
    FALSE,
    (NOW() - INTERVAL '10 days') + INTERVAL '12 hours'
),
-- Day 2
(
    'b0000000-0000-0000-0000-000000000002',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 9) + TIME '21:00:00',
    'CLEAN',
    2,
    NULL,
    'Slept early without device. Productive day.',
    'Helped a family member with groceries',
    FALSE,
    (NOW() - INTERVAL '9 days') + INTERVAL '12 hours'
),
-- Day 3 (URGE RESISTED)
(
    'b0000000-0000-0000-0000-000000000003',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 8) + TIME '23:30:00',
    'URGE_RESISTED',
    7,
    'Late night phone in bed',
    'Very close to browsing late night. Did wudu, prayed 2 Rak''ahs, and read Ayat al-Kursi. Urge faded.',
    'Istighfar 100 times after Wudu',
    FALSE,
    (NOW() - INTERVAL '8 days') + INTERVAL '14 hours'
),
-- Day 4
(
    'b0000000-0000-0000-0000-000000000004',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 7) + TIME '21:00:00',
    'CLEAN',
    1,
    NULL,
    'Feeling stronger today after surviving yesterday''s high-intensity urge.',
    'Fed a stray cat outside',
    FALSE,
    (NOW() - INTERVAL '7 days') + INTERVAL '12 hours'
),
-- Day 5
(
    'b0000000-0000-0000-0000-000000000005',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 6) + TIME '20:00:00',
    'CLEAN',
    2,
    NULL,
    'Busy day at work, kept my gaze and thoughts guarded.',
    'Gave charity ($5) online',
    FALSE,
    (NOW() - INTERVAL '6 days') + INTERVAL '11 hours'
),
-- Day 6 (PEEKED / EDGED - High Vulnerability)
(
    'b0000000-0000-0000-0000-000000000006',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 5) + TIME '22:15:00',
    'PEEKED_EDGED',
    5,
    'Social media explore page',
    'Fell into scrolling, peeked at triggering image. Edged slightly but shut it down. Need to limit screen time.',
    'Made instant Tawbah and read 2 pages of Quran',
    FALSE,
    (NOW() - INTERVAL '5 days') + INTERVAL '13 hours'
),
-- Day 7
(
    'b0000000-0000-0000-0000-000000000007',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 4) + TIME '21:00:00',
    'CLEAN',
    3,
    NULL,
    'Recovery day, stayed off social media. Spent evening at the masjid.',
    'Visited a relative',
    FALSE,
    (NOW() - INTERVAL '4 days') + INTERVAL '12 hours'
),
-- Day 8 (SLIP UP - Reset Streak, Triggers Chaser Alert)
(
    'b0000000-0000-0000-0000-000000000008',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 3) + TIME '23:45:00',
    'SLIP_UP',
    9,
    'Boredom / loneliness',
    'Fell late night. Extremely regretful. Need to reset my devices and seek mercy.',
    NULL,
    TRUE, -- Chaser alert active!
    (NOW() - INTERVAL '3 days') + INTERVAL '14 hours'
),
-- Day 9 (CLEAN, Chaser alert remains active within 48 hours)
(
    'b0000000-0000-0000-0000-000000000009',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 2) + TIME '21:00:00',
    'CLEAN',
    4,
    NULL,
    'Rebuilt my boundaries. Tawbah performed, back on track.',
    'Prayed Salat al-Tawbah + $2 Sadaqah',
    TRUE, -- Chaser alert remains active within 48h window of Day 8 slip
    (NOW() - INTERVAL '2 days') + INTERVAL '12 hours'
),
-- Day 10 (CLEAN, Chaser alert now deactivated as >48h has elapsed)
(
    'b0000000-0000-0000-0000-000000000010',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    (CURRENT_DATE - 1) + TIME '21:00:00',
    'CLEAN',
    2,
    NULL,
    'Safely out of the 48-hour chaser window. Urge intensity has normalized.',
    'Recited Surah Al-Mulk before sleeping',
    FALSE, -- Chaser alert cleared
    (NOW() - INTERVAL '1 days') + INTERVAL '12 hours'
),

-- =============================================================================
-- LOGS FOR SARAH PMO JOURNEY (c0000000-0000-0000-0000-000000000002)
-- =============================================================================
-- 8 Consecutive clean / resisted days representing a steady streak
(
    'b0000000-0000-0000-0000-000000000021',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    (CURRENT_DATE - 8) + TIME '22:00:00',
    'CLEAN',
    1,
    NULL,
    'Installed browser block filters. Reclaiming my evenings and thoughts.',
    NULL,
    FALSE,
    (NOW() - INTERVAL '8 days') + INTERVAL '13 hours'
),
(
    'b0000000-0000-0000-0000-000000000022',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    (CURRENT_DATE - 7) + TIME '22:00:00',
    'CLEAN',
    2,
    NULL,
    'Resisted trigger content. Spent time baking and writing journal entry.',
    NULL,
    FALSE,
    (NOW() - INTERVAL '7 days') + INTERVAL '13 hours'
),
(
    'b0000000-0000-0000-0000-000000000023',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    (CURRENT_DATE - 6) + TIME '22:00:00',
    'CLEAN',
    1,
    NULL,
    'Third day clean! Unlocked Nafs Ammarah Survivor milestone. Alhamdullilah.',
    NULL,
    FALSE,
    (NOW() - INTERVAL '6 days') + INTERVAL '13 hours'
),
(
    'b0000000-0000-0000-0000-000000000024',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    (CURRENT_DATE - 5) + TIME '22:00:00',
    'CLEAN',
    3,
    NULL,
    'Busy day at university. Keep thoughts guarded and stayed in public study area.',
    NULL,
    FALSE,
    (NOW() - INTERVAL '5 days') + INTERVAL '13 hours'
),
(
    'b0000000-0000-0000-0000-000000000025',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    (CURRENT_DATE - 4) + TIME '23:00:00',
    'URGE_RESISTED',
    6,
    'Triggering videos online',
    'Urge triggered by accidental video pop-up. Immediately closed browser tab and did grounding exercises.',
    NULL,
    FALSE,
    (NOW() - INTERVAL '4 days') + INTERVAL '14 hours'
),
(
    'b0000000-0000-0000-0000-000000000026',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    (CURRENT_DATE - 3) + TIME '22:00:00',
    'CLEAN',
    2,
    NULL,
    'Solid recovery. Left phone outside of the bedroom again. Routine helps.',
    NULL,
    FALSE,
    (NOW() - INTERVAL '3 days') + INTERVAL '13 hours'
),
(
    'b0000000-0000-0000-0000-000000000027',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    (CURRENT_DATE - 2) + TIME '22:00:00',
    'CLEAN',
    1,
    NULL,
    'Completed one week! Day 7 milestones unlocked. Mental clarity improving.',
    NULL,
    FALSE,
    (NOW() - INTERVAL '2 days') + INTERVAL '13 hours'
),
(
    'b0000000-0000-0000-0000-000000000028',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    (CURRENT_DATE - 1) + TIME '22:00:00',
    'CLEAN',
    2,
    NULL,
    'Steady progress. Focused on positive habit replacement and wudu.',
    NULL,
    FALSE,
    (NOW() - INTERVAL '1 days') + INTERVAL '13 hours'
),

-- =============================================================================
-- LOGS FOR JOHN STEADFAST PURITY (c0000000-0000-0000-0000-000000000003)
-- =============================================================================
-- 15-day streak simulation
(
    'b0000000-0000-0000-0000-000000000041', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 15) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Starting fresh PMO recovery. Reorganizing bedroom and phone usage.', NULL, FALSE, NOW() - INTERVAL '15 days'
),
(
    'b0000000-0000-0000-0000-000000000042', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 14) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Cravings present, drank lots of cold water, did pushups.', NULL, FALSE, NOW() - INTERVAL '14 days'
),
(
    'b0000000-0000-0000-0000-000000000043', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 13) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Day 3 milestone achieved. Peak physical withdrawal passed.', NULL, FALSE, NOW() - INTERVAL '13 days'
),
(
    'b0000000-0000-0000-0000-000000000044', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 12) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Dopamine baseline adjusting. Mind feels much clearer.', NULL, FALSE, NOW() - INTERVAL '12 days'
),
(
    'b0000000-0000-0000-0000-000000000045', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 11) + TIME '22:00:00', 'URGE_RESISTED', 6, 'Late night device usage', 'Felt urge late night. Put phone away, did wudu and slept.', NULL, FALSE, NOW() - INTERVAL '11 days'
),
(
    'b0000000-0000-0000-0000-000000000046', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 10) + TIME '21:00:00', 'CLEAN', 1, NULL, 'No triggers today. Gym and dhikr kept me stable.', NULL, FALSE, NOW() - INTERVAL '10 days'
),
(
    'b0000000-0000-0000-0000-000000000047', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 9) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Completed day 7. Unlocked Lawwamah milestone.', NULL, FALSE, NOW() - INTERVAL '9 days'
),
(
    'b0000000-0000-0000-0000-000000000048', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 8) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Temptation was present, but buddy John helped me stay focused.', NULL, FALSE, NOW() - INTERVAL '8 days'
),
(
    'b0000000-0000-0000-0000-000000000049', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 7) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Reclaiming focus. Dopamine levels resetting slowly.', NULL, FALSE, NOW() - INTERVAL '7 days'
),
(
    'b0000000-0000-0000-0000-000000000050', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 6) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Clean days accumulated. Habit loops are deconditioning.', NULL, FALSE, NOW() - INTERVAL '6 days'
),
(
    'b0000000-0000-0000-0000-000000000051', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 5) + TIME '22:00:00', 'URGE_RESISTED', 5, 'Stress after work', 'Stressful work day, took public transit instead of isolates.', NULL, FALSE, NOW() - INTERVAL '5 days'
),
(
    'b0000000-0000-0000-0000-000000000052', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 4) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Guard of sight and gaze maintained.', NULL, FALSE, NOW() - INTERVAL '4 days'
),
(
    'b0000000-0000-0000-0000-000000000053', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 3) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Mindfulness and purity goals. Steady.', NULL, FALSE, NOW() - INTERVAL '3 days'
),
(
    'b0000000-0000-0000-0000-000000000054', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 2) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Strong. Keeping the Niyyah alive.', NULL, FALSE, NOW() - INTERVAL '2 days'
),
(
    'b0000000-0000-0000-0000-000000000055', 'c0000000-0000-0000-0000-000000000003', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 
    (CURRENT_DATE - 1) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Day 15 completed clean. Purity ratio is high.', NULL, FALSE, NOW() - INTERVAL '1 days'
),

-- =============================================================================
-- LOGS FOR ALEX 30-DAY PURITY CHALLENGE (c0000000-0000-0000-0000-000000000004 - GRADUATED)
-- =============================================================================
-- 21 logs simulating the graduation success of his initial challenge (he logged up to graduation point)
(
    'b0000000-0000-0000-0000-000000000081', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 30) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Starting kickstart PMO challenge.', NULL, FALSE, NOW() - INTERVAL '30 days'
),
(
    'b0000000-0000-0000-0000-000000000082', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 29) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Felt minor urges, resolved them by going for a run.', NULL, FALSE, NOW() - INTERVAL '29 days'
),
(
    'b0000000-0000-0000-0000-000000000083', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 28) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Day 3 completed clean.', NULL, FALSE, NOW() - INTERVAL '28 days'
),
(
    'b0000000-0000-0000-0000-000000000084', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 27) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Energy and focus levels returning to normal.', NULL, FALSE, NOW() - INTERVAL '27 days'
),
(
    'b0000000-0000-0000-0000-000000000085', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 26) + TIME '22:00:00', 'URGE_RESISTED', 5, 'Boredom', 'Urge triggered by boredom, left my laptop outside and walked.', NULL, FALSE, NOW() - INTERVAL '26 days'
),
(
    'b0000000-0000-0000-0000-000000000086', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 25) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Gym session kept me busy and exhausted.', NULL, FALSE, NOW() - INTERVAL '25 days'
),
(
    'b0000000-0000-0000-0000-000000000087', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 24) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Day 7 clean milestone reached. Purity reset foundation solid.', NULL, FALSE, NOW() - INTERVAL '24 days'
),
(
    'b0000000-0000-0000-0000-000000000088', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 23) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Recovery days accumulating.', NULL, FALSE, NOW() - INTERVAL '23 days'
),
(
    'b0000000-0000-0000-0000-000000000089', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 22) + TIME '21:00:00', 'CLEAN', 1, NULL, 'No triggers today. Productive programming.', NULL, FALSE, NOW() - INTERVAL '22 days'
),
(
    'b0000000-0000-0000-0000-000000000090', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 21) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Feeling healthy, sleeping well.', NULL, FALSE, NOW() - INTERVAL '21 days'
),
(
    'b0000000-0000-0000-0000-000000000091', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 20) + TIME '22:00:00', 'URGE_RESISTED', 4, 'Boredom', 'Tense weekend slot, avoided browsing, read Quran.', NULL, FALSE, NOW() - INTERVAL '20 days'
),
(
    'b0000000-0000-0000-0000-000000000092', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 19) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Stable baseline. Social interactions helping.', NULL, FALSE, NOW() - INTERVAL '19 days'
),
(
    'b0000000-0000-0000-0000-000000000093', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 18) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Guarded eyes and environment.', NULL, FALSE, NOW() - INTERVAL '18 days'
),
(
    'b0000000-0000-0000-0000-000000000094', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 17) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Steadfast progression.', NULL, FALSE, NOW() - INTERVAL '17 days'
),
(
    'b0000000-0000-0000-0000-000000000095', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 16) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Guarding mental hygiene.', NULL, FALSE, NOW() - INTERVAL '16 days'
),
(
    'b0000000-0000-0000-0000-000000000096', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 15) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Day 15 completed, half way to kickstart target.', NULL, FALSE, NOW() - INTERVAL '15 days'
),
(
    'b0000000-0000-0000-0000-000000000097', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 14) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Reclaimed mental focus. Self-control is high.', NULL, FALSE, NOW() - INTERVAL '14 days'
),
(
    'b0000000-0000-0000-0000-000000000098', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 13) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Purity transformation progress.', NULL, FALSE, NOW() - INTERVAL '13 days'
),
(
    'b0000000-0000-0000-0000-000000000099', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 12) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Steadfast. Day 21 rewiring achieved.', NULL, FALSE, NOW() - INTERVAL '12 days'
),
(
    'b0000000-0000-0000-0000-0000000000aa', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 11) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Almost at the 30-day mark. Purity feels great.', NULL, FALSE, NOW() - INTERVAL '11 days'
),
(
    'b0000000-0000-0000-0000-0000000000ab', 'c0000000-0000-0000-0000-000000000004', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 
    (CURRENT_DATE - 10) + TIME '21:00:00', 'CLEAN', 1, NULL, 'Day 30 completed clean! Graduated initial challenge successfully.', NULL, FALSE, NOW() - INTERVAL '10 days'
),

-- =============================================================================
-- LOGS FOR SARAH PMO RESET (c0000000-0000-0000-0000-000000000005 - ARCHIVED)
-- =============================================================================
(
    'b0000000-0000-0000-0000-0000000000c1', 'c0000000-0000-0000-0000-000000000005', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 
    (CURRENT_DATE - 25) + TIME '21:00:00', 'CLEAN', 1, NULL, 'First day of purity journey.', NULL, FALSE, NOW() - INTERVAL '25 days'
),
(
    'b0000000-0000-0000-0000-0000000000c2', 'c0000000-0000-0000-0000-000000000005', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 
    (CURRENT_DATE - 24) + TIME '21:00:00', 'CLEAN', 2, NULL, 'Felt fine. Guided my thoughts productively.', NULL, FALSE, NOW() - INTERVAL '24 days'
),
(
    'b0000000-0000-0000-0000-0000000000c3', 'c0000000-0000-0000-0000-000000000005', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 
    (CURRENT_DATE - 23) + TIME '21:00:00', 'SLIP_UP', 7, 'Late night phone in bed', 'Slipped up late night due to device. Need proper block rules.', NULL, FALSE, NOW() - INTERVAL '23 days'
),
(
    'b0000000-0000-0000-0000-0000000000c4', 'c0000000-0000-0000-0000-000000000005', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 
    (CURRENT_DATE - 22) + TIME '21:00:00', 'CLEAN', 3, NULL, 'Restarted. Archiving this unorganized chain to start a structured one.', NULL, FALSE, NOW() - INTERVAL '22 days'
);

-- -----------------------------------------------------------------------------
-- 6. Emergency SOS Sessions Seeding
-- -----------------------------------------------------------------------------
INSERT INTO emergency_sessions (
    id, chain_id, user_id, session_type, technique_used, craving_before, craving_after, duration_seconds, created_at
) VALUES
-- Alex PMO Chain: Session 1 (Spiritual Survived)
(
    'e0000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'SPIRITUAL',
    'Wudu + Ayat al-Kursi Recitation',
    8,
    2,
    180,
    (NOW() - INTERVAL '8 days') - INTERVAL '30 minutes'
),
-- Alex PMO Chain: Session 2 (Psychological Survived)
(
    'e0000000-0000-0000-0000-000000000002',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'PSYCHOLOGICAL',
    'Box Breathing Technique',
    6,
    3,
    120,
    (NOW() - INTERVAL '5 days') - INTERVAL '15 minutes'
),
-- Sarah PMO Chain: Session 3 (Physical Circuit Breaker Survived)
(
    'e0000000-0000-0000-0000-000000000003',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'PHYSICAL_CIRCUIT_BREAKER',
    '5-4-3-2-1 Sensory Grounding',
    7,
    2,
    90,
    (NOW() - INTERVAL '4 days') - INTERVAL '45 minutes'
);

-- -----------------------------------------------------------------------------
-- 7. Accountability Partnerships Seeding
-- -----------------------------------------------------------------------------
INSERT INTO accountability_partnerships (
    id, chain_id, user_id, partner_user_id, role, invite_code, status, created_at
) VALUES
-- Alex PMO Chain accepts Sheikh Ahmad as Mentor (ACCEPTED)
(
    'd0000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'SPIRITUAL_MENTOR',
    'BC-MENTOR',
    'ACCEPTED',
    NOW() - INTERVAL '10 days'
),
-- Alex PMO Chain links Partner John as Peer Partner (ACCEPTED)
(
    'd0000000-0000-0000-0000-000000000002',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'PEER_BUDDY',
    'BC-BUDDY',
    'ACCEPTED',
    NOW() - INTERVAL '10 days'
),
-- Sarah PMO Chain links Partner John as Partner (ACCEPTED)
(
    'd0000000-0000-0000-0000-000000000003',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'PEER_BUDDY',
    'BC-SCROLL',
    'ACCEPTED',
    NOW() - INTERVAL '8 days'
),
-- John PMO Chain invite code created but not accepted (PENDING)
(
    'd0000000-0000-0000-0000-000000000004',
    'c0000000-0000-0000-0000-000000000003',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    NULL,
    'PEER_BUDDY',
    'BC-PEND1',
    'PENDING',
    NOW() - INTERVAL '4 days'
);

-- -----------------------------------------------------------------------------
-- 8. Counsel Notes (Nasiha Feed) Seeding
-- -----------------------------------------------------------------------------
INSERT INTO counsel_notes (
    id, chain_id, mentor_user_id, user_id, note_content, created_at
) VALUES
-- First counsel note by Sheikh Ahmad (Encouragement on day 2)
(
    'a0000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Assalamu Alaikum, Alex. May Allah grant you steadfastness. The first stage of recovery (Nafs al-Ammarah) requires building strong behavioral boundaries. Keep your device outside of your room before sleeping, and ensure you recite the evening Adhkar. I will keep you in my prayers.',
    NOW() - INTERVAL '9 days'
),
-- Second counsel note by Sheikh Ahmad (After the slip-up on day 8)
(
    'a0000000-0000-0000-0000-000000000002',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Do not let regret turn into despair. Tawbah is a gift that washes away the slip. Perform wudu, stand in prayer, and remember that Allah loves the strivings of the penitent. Let''s analyze what triggered this and double your protective measures tonight.',
    NOW() - INTERVAL '2 days'
);

-- -----------------------------------------------------------------------------
-- 9. Milestone Badges Seeding
-- -----------------------------------------------------------------------------
INSERT INTO milestone_badges (
    id, chain_id, user_id, badge_type, achieved_at
) VALUES
-- Alex PMO Chain: achieved Nafs Ammarah Survivor (Day 3 milestone)
(
    'b1000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'NAFS_AMMARAH_SURVIVOR',
    (CURRENT_DATE - 8) + TIME '10:00:00'
),
-- Sarah PMO Chain: achieved Nafs Ammarah Survivor (Day 3)
(
    'b1000000-0000-0000-0000-000000000002',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'NAFS_AMMARAH_SURVIVOR',
    (CURRENT_DATE - 6) + TIME '10:00:00'
),
-- Sarah PMO Chain: achieved Nafs Lawwamah Striver (Day 7)
(
    'b1000000-0000-0000-0000-000000000003',
    'c0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'NAFS_LAWWAMAH_STRIVER',
    (CURRENT_DATE - 2) + TIME '10:00:00'
),
-- John PMO Chain: achieved Nafs Ammarah Survivor (Day 3)
(
    'b1000000-0000-0000-0000-000000000004',
    'c0000000-0000-0000-0000-000000000003',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'NAFS_AMMARAH_SURVIVOR',
    (CURRENT_DATE - 13) + TIME '10:00:00'
),
-- John PMO Chain: achieved Nafs Lawwamah Striver (Day 7)
(
    'b1000000-0000-0000-0000-000000000005',
    'c0000000-0000-0000-0000-000000000003',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'NAFS_LAWWAMAH_STRIVER',
    (CURRENT_DATE - 9) + TIME '10:00:00'
),
-- Alex PMO Graduated Kickstart: achieved Nafs Ammarah Survivor (Day 3)
(
    'b1000000-0000-0000-0000-000000000006',
    'c0000000-0000-0000-0000-000000000004',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'NAFS_AMMARAH_SURVIVOR',
    (CURRENT_DATE - 28) + TIME '10:00:00'
),
-- Alex PMO Graduated Kickstart: achieved Nafs Lawwamah Striver (Day 7)
(
    'b1000000-0000-0000-0000-000000000007',
    'c0000000-0000-0000-0000-000000000004',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'NAFS_LAWWAMAH_STRIVER',
    (CURRENT_DATE - 24) + TIME '10:00:00'
),
-- Alex PMO Graduated Kickstart: achieved Neural Rewire 21 (Day 21)
(
    'b1000000-0000-0000-0000-000000000008',
    'c0000000-0000-0000-0000-000000000004',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'NEURAL_REWIRE_21',
    (CURRENT_DATE - 10) + TIME '10:00:00'
);

-- -----------------------------------------------------------------------------
-- 10. Partner Messages (2-Way Mentorship Chat History) Seeding
-- -----------------------------------------------------------------------------
INSERT INTO partner_messages (
    id, partnership_id, sender_id, message_content, is_read, created_at
) VALUES
-- Messages between Alex and Sheikh Ahmad (partnership_id: d0000000-0000-0000-0000-000000000001)
(
    'f0000000-0000-0000-0000-000000000001',
    'd0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', -- Sender: Alex
    'Assalamu Alaikum Sheikh, the cravings are extremely intense tonight. I am feeling restless.',
    TRUE,
    (NOW() - INTERVAL '8 days') - INTERVAL '50 minutes'
),
(
    'f0000000-0000-0000-0000-000000000002',
    'd0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', -- Sender: Sheikh Ahmad
    'Wa Alaikum Assalam, my dear son. Stand up immediately. Go splash cold water on your face, perform fresh wudu, and recite the 3 Quls. The physical urge will pass in a few minutes. Guard your gaze.',
    TRUE,
    (NOW() - INTERVAL '8 days') - INTERVAL '45 minutes'
),
(
    'f0000000-0000-0000-0000-000000000003',
    'd0000000-0000-0000-0000-000000000001',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', -- Sender: Alex
    'JazakAllah Khair Sheikh. The wudu helped tremendously. The urge intensity dropped. I am safe now.',
    TRUE,
    (NOW() - INTERVAL '8 days') - INTERVAL '25 minutes'
),

-- Messages between Alex and Partner John (partnership_id: d0000000-0000-0000-0000-000000000002)
(
    'f0000000-0000-0000-0000-000000000004',
    'd0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', -- Sender: Alex
    'Hey John, how is your PMO recovery streak going? I''m 4 days clean today.',
    TRUE,
    NOW() - INTERVAL '7 days'
),
(
    'f0000000-0000-0000-0000-000000000005',
    'd0000000-0000-0000-0000-000000000002',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', -- Sender: Partner John
    'Going great bro, day 9 over here! Keep guarding your eyes. We''ve got this.',
    TRUE,
    (NOW() - INTERVAL '7 days') + INTERVAL '15 minutes'
);

-- -----------------------------------------------------------------------------
-- 11. Refresh Tokens Seeding (Simulating active sessions)
-- -----------------------------------------------------------------------------
INSERT INTO refresh_tokens (
    id, token, user_id, expires_at, created_at
) VALUES
-- Alex Smith's active refresh token
(
    'e1000000-0000-0000-0000-000000000001',
    'd8e9f0a1-b2c3-4d5e-6f7a-8b9c0d1e2f3a',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    NOW() + INTERVAL '7 days',
    NOW()
),
-- Sheikh Ahmad's active refresh token
(
    'e1000000-0000-0000-0000-000000000002',
    'd8e9f0a1-b2c3-4d5e-6f7a-8b9c0d1e2f3b',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    NOW() + INTERVAL '7 days',
    NOW()
);

COMMIT;
