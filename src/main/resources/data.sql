SELECT 'DATA.SQL LOADED' AS test_message;


-- Enable pgcrypto for UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Only insert Varata Inc. if not already present
INSERT INTO lms_app.lms_user (id, name, email, password, userRole, locked, enabled, delta_password)
SELECT gen_random_uuid(),
       'Varata Inc.',
       'varata@demo.com',
       '$2a$10$Dow1UMLUlzEuROvQ9CqKyeAmQmsfTXLz5dJ/UfECG2lKJoRzqTDsm', -- "password"
       'SUPER_ADMIN',
       0, 1, 0
WHERE NOT EXISTS (
    SELECT 1 FROM lms_app.lms_user WHERE email = 'varata@demo.com'
);

-- Insert 20 dummy users (idempotent)
INSERT INTO lms_app.lms_user (id, name, email, password, userRole, locked, enabled, delta_password)
SELECT gen_random_uuid(),
       'User ' || i,
       'user' || i || '@demo.com',
       '$2a$10$7QxQdEPDx5lEieql8A4ZSO92mVAXKAjAo6f5STTiihv4DJKYgh1cK', -- same bcrypt hash
       'USER',
       0, 1, 0
FROM generate_series(1, 20) AS s(i)
WHERE NOT EXISTS (
    SELECT 1 FROM lms_app.lms_user WHERE email = 'user' || i || '@demo.com'
);
