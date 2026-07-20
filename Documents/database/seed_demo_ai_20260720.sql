-- Demo seed for admin statistics and AI recommendations.
-- NOT executed automatically. Review before running against any database.
-- Scope: +40 companies, +200 jobs, +50 applications, +100 transactions.
-- All demo rows are identified by DEMO_AI_20260720.
-- Run in a transaction. The cleanup section removes only demo rows created by this file.

BEGIN;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM companies WHERE company_name LIKE 'DEMO_AI_20260720_%') THEN
        RAISE EXCEPTION 'Demo seed already exists. Run cleanup first or use a new marker.';
    END IF;
END $$;

CREATE TEMP TABLE demo_companies (id bigint PRIMARY KEY) ON COMMIT DROP;
CREATE TEMP TABLE demo_employers (id bigint PRIMARY KEY, user_id bigint NOT NULL, company_id bigint NOT NULL) ON COMMIT DROP;
CREATE TEMP TABLE demo_jobs (id bigint PRIMARY KEY, company_id integer NOT NULL, employer_id bigint NOT NULL) ON COMMIT DROP;
CREATE TEMP TABLE demo_candidates (id bigint PRIMARY KEY, user_id bigint NOT NULL, cv_id bigint NOT NULL) ON COMMIT DROP;
CREATE TEMP TABLE demo_skills (id bigint PRIMARY KEY) ON COMMIT DROP;

-- Reuse the existing employer/candidate users and create one additional demo candidate.
-- Password is a bcrypt hash for demo-only usage; it must not be used in production.
INSERT INTO users (username, password, full_name, email, role_id, created_at, updated_at)
SELECT 'demo_ai_candidate_20260720', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Demo AI Candidate', 'demo_ai_candidate_20260720@example.test', r.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM roles r
WHERE r.role_name = 'CANDIDATE'
ON CONFLICT (username) DO NOTHING;

INSERT INTO candidates (user_id, desired_min_salary, desired_max_salary, created_at, updated_at)
SELECT u.id, 15000000, 35000000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users u
WHERE u.username = 'demo_ai_candidate_20260720'
  AND NOT EXISTS (SELECT 1 FROM candidates c WHERE c.user_id = u.id);

INSERT INTO cvs (candidate_id, cv_title, file_url, description, version, created_at, updated_at)
SELECT c.id, 'DEMO_AI_20260720 CV', 'https://example.test/demo-ai-cv.pdf', 'Demo CV for AI recommendation testing', '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM candidates c
JOIN users u ON u.id = c.user_id
WHERE u.username = 'demo_ai_candidate_20260720'
  AND NOT EXISTS (SELECT 1 FROM cvs cv WHERE cv.candidate_id = c.id AND cv.cv_title = 'DEMO_AI_20260720 CV');

INSERT INTO demo_candidates (id, user_id, cv_id)
SELECT c.id, c.user_id, cv.id
FROM candidates c
JOIN users u ON u.id = c.user_id
JOIN cvs cv ON cv.candidate_id = c.id AND cv.cv_title = 'DEMO_AI_20260720 CV'
WHERE u.username = 'demo_ai_candidate_20260720';

-- Add reusable AI skills. Existing names are reused when present.
INSERT INTO skills (skill_name, created_at, updated_at)
SELECT v.skill_name, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
  ('DEMO_AI_20260720 Java'), ('DEMO_AI_20260720 Spring Boot'), ('DEMO_AI_20260720 React'),
  ('DEMO_AI_20260720 JavaScript'), ('DEMO_AI_20260720 Python'), ('DEMO_AI_20260720 FastAPI'),
  ('DEMO_AI_20260720 PostgreSQL'), ('DEMO_AI_20260720 Docker'), ('DEMO_AI_20260720 AWS'),
  ('DEMO_AI_20260720 Data Analysis'), ('DEMO_AI_20260720 QA Automation'), ('DEMO_AI_20260720 UI UX'
)) AS v(skill_name)
WHERE NOT EXISTS (SELECT 1 FROM skills s WHERE s.skill_name = v.skill_name);

INSERT INTO demo_skills (id)
SELECT id FROM skills WHERE skill_name LIKE 'DEMO_AI_20260720_%';

INSERT INTO candidate_skills (candidate_id, skill_id, created_at, updated_at)
SELECT dc.id, ds.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM demo_candidates dc CROSS JOIN demo_skills ds
ON CONFLICT (candidate_id, skill_id) DO NOTHING;

-- Create 40 companies.
WITH inserted AS (
    INSERT INTO companies (company_name, description, created_at, updated_at)
    SELECT 'DEMO_AI_20260720_Company_' || lpad(g::text, 2, '0'),
           'Demo company ' || g || ' for statistics and AI recommendation testing',
           CURRENT_TIMESTAMP - ((g % 12) || ' months')::interval,
           CURRENT_TIMESTAMP
    FROM generate_series(1, 40) g
    RETURNING id
)
INSERT INTO demo_companies (id) SELECT id FROM inserted;

-- Create one employer user/employer per company.
WITH company_rows AS (
    SELECT id, row_number() OVER (ORDER BY id) AS n FROM demo_companies
), new_users AS (
    INSERT INTO users (username, password, full_name, email, role_id, created_at, updated_at)
    SELECT 'demo_ai_employer_20260720_' || lpad(n::text, 2, '0'),
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           'Demo Employer ' || n,
           'demo_ai_employer_20260720_' || lpad(n::text, 2, '0') || '@example.test',
           (SELECT id FROM roles WHERE role_name = 'EMPLOYER'),
           CURRENT_TIMESTAMP - ((n % 12) || ' months')::interval,
           CURRENT_TIMESTAMP
    FROM company_rows
    RETURNING id, username
)
INSERT INTO demo_employers (id, user_id, company_id)
SELECT e.id, u.id, e.company_id
FROM employers e
JOIN new_users u ON u.id = e.user_id
WHERE u.username LIKE 'demo_ai_employer_20260720_%';

-- The previous insert creates users but not employer rows; create them and capture IDs.
INSERT INTO employers (user_id, company_id, created_at, updated_at, role_id)
SELECT u.id, c.id, u.created_at, CURRENT_TIMESTAMP, (SELECT id FROM roles WHERE role_name = 'EMPLOYER')
FROM users u
JOIN demo_companies c ON c.id = (SELECT id FROM demo_companies ORDER BY id LIMIT 1 OFFSET ((split_part(u.username, '_', 5))::int - 1))
WHERE u.username LIKE 'demo_ai_employer_20260720_%'
  AND NOT EXISTS (SELECT 1 FROM employers e WHERE e.user_id = u.id);

TRUNCATE demo_employers;
INSERT INTO demo_employers (id, user_id, company_id)
SELECT e.id, e.user_id, e.company_id
FROM employers e JOIN users u ON u.id = e.user_id
WHERE u.username LIKE 'demo_ai_employer_20260720_%';

-- Create 200 jobs, 5 jobs per demo company, with skills and salary ranges for AI.
WITH job_rows AS (
    SELECT n, ((n - 1) / 5 + 1)::bigint AS company_n, ((n - 1) % 5 + 1)::int AS variant
    FROM generate_series(1, 200) n
), inserted AS (
    INSERT INTO jobs (employer_id, title, description, location, experience, quantity, posted_at, expired_at, application_deadline, status, is_hidden_on_expiry, created_at, updated_at, min_salary, max_salary, currency, company_id)
    SELECT de.id,
           CASE jr.variant % 6 WHEN 0 THEN 'Backend Java Engineer' WHEN 1 THEN 'Frontend React Developer' WHEN 2 THEN 'Python Data Engineer' WHEN 3 THEN 'QA Automation Engineer' WHEN 4 THEN 'Full Stack Developer' ELSE 'DevOps Cloud Engineer' END || ' - DEMO_AI_20260720_' || jr.n,
           'DEMO_AI_20260720 job. Build software with Java Spring Boot React Python PostgreSQL Docker and cloud technologies.',
           CASE jr.n % 5 WHEN 0 THEN 'Ha Noi' WHEN 1 THEN 'Ho Chi Minh' WHEN 2 THEN 'Da Nang' WHEN 3 THEN 'Can Tho' ELSE 'Remote' END,
           CASE jr.variant % 3 WHEN 0 THEN '1-2 years' WHEN 1 THEN '2-4 years' ELSE '4+ years' END,
           1 + (jr.n % 3), CURRENT_TIMESTAMP - ((jr.n % 12) || ' months')::interval,
           CURRENT_TIMESTAMP + ((30 + jr.n % 180) || ' days')::interval,
           CURRENT_TIMESTAMP + ((15 + jr.n % 120) || ' days')::interval,
           CASE jr.n % 4 WHEN 0 THEN 1 WHEN 1 THEN 1 WHEN 2 THEN 2 ELSE 4 END,
           false, CURRENT_TIMESTAMP - ((jr.n % 12) || ' months')::interval, CURRENT_TIMESTAMP,
           12000000 + ((jr.n % 8) * 2000000), 22000000 + ((jr.n % 8) * 3000000), 'VND', dc.id
    FROM job_rows jr
    JOIN (SELECT id, company_id, row_number() OVER (ORDER BY id) AS n FROM demo_employers) de ON de.n = jr.company_n
    JOIN demo_companies dc ON dc.id = de.company_id
    RETURNING id, company_id, employer_id
)
INSERT INTO demo_jobs (id, company_id, employer_id) SELECT id, company_id, employer_id FROM inserted;

INSERT INTO job_skills (job_id, skill_id, created_at, updated_at)
SELECT dj.id, ds.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM demo_jobs dj
JOIN LATERAL (SELECT id FROM demo_skills ORDER BY id OFFSET ((dj.id % 12)) LIMIT 3) ds ON true
ON CONFLICT (job_id, skill_id) DO NOTHING;

-- 50 applications spread across demo jobs, using the demo candidate CV.
INSERT INTO applications (candidate_id, job_id, cv_id, applied_at, cover_letter, description, status, created_at, updated_at)
SELECT dc.id, dj.id, dc.cv_id, CURRENT_TIMESTAMP - ((n % 12) || ' months')::interval,
       'DEMO_AI_20260720 application', 'Demo application for AI/statistics testing',
       CASE n % 4 WHEN 0 THEN 2 WHEN 1 THEN 1 WHEN 2 THEN 3 ELSE 0 END,
       CURRENT_TIMESTAMP - ((n % 12) || ' months')::interval, CURRENT_TIMESTAMP
FROM generate_series(1, 50) n
CROSS JOIN LATERAL (SELECT id, cv_id FROM demo_candidates LIMIT 1) dc
JOIN LATERAL (SELECT id FROM demo_jobs ORDER BY id OFFSET ((n - 1) % 200) LIMIT 1) dj ON true;

-- 100 transactions with successful, pending and failed states across existing employer/candidate users.
INSERT INTO transactions (user_id, transaction_type, amount, status, content, created_at, updated_at, package_id, txn_ref, payment_status, payment_provider, provider_transaction_id)
SELECT u.id,
       'DEMO_AI_20260720_VIP',
       CASE n % 4 WHEN 0 THEN 99000 WHEN 1 THEN 199000 WHEN 2 THEN 249000 ELSE 499000 END,
       CASE n % 10 WHEN 0 THEN 0 WHEN 1 THEN 2 ELSE 1 END,
       'DEMO_AI_20260720 demo spending transaction',
       CURRENT_TIMESTAMP - ((n % 12) || ' months')::interval, CURRENT_TIMESTAMP,
       CASE n % 4 WHEN 0 THEN 1 WHEN 1 THEN 3 WHEN 2 THEN 2 ELSE 4 END,
       'DEMO_AI_20260720_TXN_' || lpad(n::text, 3, '0'),
       CASE n % 10 WHEN 0 THEN 'PENDING' WHEN 1 THEN 'FAILED' ELSE 'SUCCESS' END,
       'DEMO_SEED', 'DEMO_AI_20260720_PROVIDER_' || lpad(n::text, 3, '0')
FROM generate_series(1, 100) n
JOIN LATERAL (SELECT id FROM users WHERE role_id IN (2, 3) ORDER BY id OFFSET ((n - 1) % (SELECT count(*) FROM users WHERE role_id IN (2, 3))) LIMIT 1) u ON true;

-- Keep sequences above manually inserted rows safe if the database has custom sequence state.
SELECT setval(pg_get_serial_sequence('companies', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM companies), nextval(pg_get_serial_sequence('companies', 'id'))));
SELECT setval(pg_get_serial_sequence('users', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM users), nextval(pg_get_serial_sequence('users', 'id'))));
SELECT setval(pg_get_serial_sequence('employers', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM employers), nextval(pg_get_serial_sequence('employers', 'id'))));
SELECT setval(pg_get_serial_sequence('jobs', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM jobs), nextval(pg_get_serial_sequence('jobs', 'id'))));
SELECT setval(pg_get_serial_sequence('cvs', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM cvs), nextval(pg_get_serial_sequence('cvs', 'id'))));
SELECT setval(pg_get_serial_sequence('applications', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM applications), nextval(pg_get_serial_sequence('applications', 'id'))));
SELECT setval(pg_get_serial_sequence('transactions', 'id'), GREATEST((SELECT COALESCE(MAX(id), 1) FROM transactions), nextval(pg_get_serial_sequence('transactions', 'id'))));

COMMIT;

-- Cleanup recipe (run separately only if the demo seed was executed):
-- BEGIN;
-- DELETE FROM applications WHERE cover_letter = 'DEMO_AI_20260720 application';
-- DELETE FROM transactions WHERE txn_ref LIKE 'DEMO_AI_20260720_TXN_%';
-- DELETE FROM job_skills WHERE job_id IN (SELECT id FROM jobs WHERE title LIKE '%DEMO_AI_20260720_%');
-- DELETE FROM jobs WHERE title LIKE '%DEMO_AI_20260720_%';
-- DELETE FROM employers WHERE user_id IN (SELECT id FROM users WHERE username LIKE 'demo_ai_employer_20260720_%');
-- DELETE FROM cvs WHERE cv_title = 'DEMO_AI_20260720 CV';
-- DELETE FROM candidate_skills WHERE candidate_id IN (SELECT c.id FROM candidates c JOIN users u ON u.id = c.user_id WHERE u.username = 'demo_ai_candidate_20260720');
-- DELETE FROM candidates WHERE user_id IN (SELECT id FROM users WHERE username = 'demo_ai_candidate_20260720');
-- DELETE FROM users WHERE username LIKE 'demo_ai_employer_20260720_%' OR username = 'demo_ai_candidate_20260720';
-- DELETE FROM skills WHERE skill_name LIKE 'DEMO_AI_20260720_%';
-- DELETE FROM companies WHERE company_name LIKE 'DEMO_AI_20260720_%';
-- COMMIT;
