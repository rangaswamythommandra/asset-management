-- Fix passwords in the database
-- This script will update the passwords with the correct BCrypt hashes

-- Update admin password to: admin123
UPDATE user SET password = '$2a$10$AKlJXMZ6goBvPa7bd/DaK.p.l0QaDjCu5HzsI8hSu50bKyWYfvHI.' WHERE username = 'admin';

-- Update commander1 password to: commander123
UPDATE user SET password = '$2a$10$6MqA3Ycu7hTt6mWJvjDShOlbDhmZfVmbyi87qBiW8eJYYTQmxJWJG' WHERE username = 'commander1';

-- Update logistics1 password to: logistics123
UPDATE user SET password = '$2a$10$u/D3HQ.Y/Q69aFW81t6Wr.oc5RVQ2PWnPnwQolDpUsKsc6O3wkdue' WHERE username = 'logistics1';

-- Verify the updates
SELECT username, password FROM user WHERE username IN ('admin', 'commander1', 'logistics1'); 