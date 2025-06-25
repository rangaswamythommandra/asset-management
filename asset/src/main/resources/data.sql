-- Insert sample bases
INSERT INTO base (id, name, location) VALUES 
(1, 'Fort Hood', 'Killeen, Texas'),
(2, 'Fort Bragg', 'Fayetteville, North Carolina'),
(3, 'Fort Campbell', 'Clarksville, Tennessee'),
(4, 'Fort Benning', 'Columbus, Georgia'),
(5, 'Fort Stewart', 'Hinesville, Georgia');

-- Insert sample asset types
INSERT INTO asset_type (id, name, category) VALUES 
(1, 'Tank', 'VEHICLE'),
(2, 'Humvee', 'VEHICLE'),
(3, 'Rifle', 'WEAPON'),
(4, 'Radio', 'ELECTRONICS'),
(5, 'Medical Kit', 'MEDICAL'),
(6, 'Computer', 'ELECTRONICS'),
(7, 'Generator', 'EQUIPMENT'),
(8, 'Tent', 'EQUIPMENT');

-- Insert users with updated passwords
-- admin password: admin123
-- commander1 password: commander123  
-- logistics1 password: logistics123
INSERT INTO user (id, username, password, role, base_id) VALUES 
(1, 'admin', '$2a$10$AKlJXMZ6goBvPa7bd/DaK.p.l0QaDjCu5HzsI8hSu50bKyWYfvHI.', 'ADMIN', 1),
(2, 'commander1', '$2a$10$6MqA3Ycu7hTt6mWJvjDShOlbDhmZfVmbyi87qBiW8eJYYTQmxJWJG', 'BASE_COMMANDER', 1),
(3, 'logistics1', '$2a$10$u/D3HQ.Y/Q69aFW81t6Wr.oc5RVQ2PWnPnwQolDpUsKsc6O3wkdue', 'LOGISTICS_OFFICER', 1);

-- Insert sample assets
INSERT INTO asset (id, serial_number, status, asset_type_id, base_id) VALUES 
(1, 'TANK-001', 'ACTIVE', 1, 1),
(2, 'TANK-002', 'ACTIVE', 1, 1),
(3, 'HUMVEE-001', 'ACTIVE', 2, 1),
(4, 'HUMVEE-002', 'MAINTENANCE', 2, 1),
(5, 'RIFLE-001', 'ACTIVE', 3, 1),
(6, 'RIFLE-002', 'ACTIVE', 3, 1),
(7, 'RADIO-001', 'ACTIVE', 4, 1),
(8, 'COMP-001', 'ACTIVE', 6, 1),
(9, 'COMP-002', 'ACTIVE', 6, 1),
(10, 'GEN-001', 'ACTIVE', 7, 1);

-- Insert sample purchases
INSERT INTO purchase (id, date, quantity, unit_price, total_amount, supplier, description, asset_type_id, base_id, created_by) VALUES 
(1, '2024-01-15', 5, 2500000.00, 12500000.00, 'General Dynamics', 'Main battle tanks for armored division', 1, 1, 1),
(2, '2024-02-20', 10, 150000.00, 1500000.00, 'AM General', 'High Mobility Multipurpose Wheeled Vehicles', 2, 1, 1),
(3, '2024-03-10', 50, 1200.00, 60000.00, 'Colt Defense', 'M4 carbines for infantry units', 3, 1, 1),
(4, '2024-04-05', 20, 5000.00, 100000.00, 'Harris Corporation', 'Tactical communication radios', 4, 1, 1),
(5, '2024-05-12', 15, 2500.00, 37500.00, 'Dell Technologies', 'Desktop computers for command center', 6, 1, 1);

-- Insert sample transfers
INSERT INTO transfer (id, date, reason, status, asset_id, from_base_id, to_base_id, created_by) VALUES 
(1, '2024-06-01', 'Reallocation for training exercise', 'COMPLETED', 1, 1, 2, 1),
(2, '2024-06-15', 'Equipment maintenance transfer', 'APPROVED', 3, 1, 3, 1),
(3, '2024-07-01', 'New unit assignment', 'PENDING', 5, 1, 4, 1);

-- Insert sample assignments
INSERT INTO assignment (id, assigned_date, return_date, status, notes, asset_id, assigned_to, assigned_by) VALUES 
(1, '2024-01-20', NULL, 'ACTIVE', 'Assigned for training exercise', 1, 2, 1),
(2, '2024-02-01', '2024-02-15', 'RETURNED', 'Completed maintenance task', 3, 3, 1),
(3, '2024-03-01', NULL, 'ACTIVE', 'New personnel assignment', 5, 2, 1),
(4, '2024-04-01', NULL, 'ACTIVE', 'Command center equipment', 7, 3, 1);

-- Insert sample expenditures
INSERT INTO expenditure (id, expenditure_date, quantity, reason, asset_id, base_id, approved_by) VALUES 
(1, '2024-01-25', 1, 'Fuel consumption for training exercise', 1, 1, 1),
(2, '2024-02-10', 1, 'Maintenance and repair costs', 4, 1, 1),
(3, '2024-03-15', 1, 'Ammunition used in training', 5, 1, 1),
(4, '2024-04-20', 1, 'Battery replacement', 7, 1, 1);

-- Insert sample audit logs
INSERT INTO audit_log (id, timestamp, action, entity, entity_id, details, user_id) VALUES 
(1, '2024-01-15 10:30:00', 'CREATE', 'PURCHASE', 1, 'Created new purchase order for 5 tanks', 1),
(2, '2024-01-20 14:15:00', 'ASSIGN', 'ASSET', 1, 'Assigned tank TANK-001 to Sgt. John Smith', 2),
(3, '2024-02-01 09:45:00', 'TRANSFER', 'TRANSFER', 1, 'Transferred 2 tanks from Fort Hood to Fort Bragg', 1),
(4, '2024-03-01 16:20:00', 'MAINTENANCE', 'ASSET', 4, 'Asset HUMVEE-002 placed under maintenance', 3);

UPDATE user SET password = '$2a$10$6MqA3Ycu7hTt6mWJvjDShOlbDhmZfVmbyi87qBiW8eJYYTQmxJWJG' WHERE username = 'commander1';
UPDATE user SET password = '$2a$10$u/D3HQ.Y/Q69aFW81t6Wr.oc5RVQ2PWnPnwQolDpUsKsc6O3wkdue' WHERE username = 'logistics1';