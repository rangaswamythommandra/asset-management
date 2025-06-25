-- Example user insert statements (update these lines only)
-- Replace the existing password values for these users with the following hashes:
-- admin:      $2b$12$CfbH8/2DLDrCkHc5Ye7/r.ZvGrgFptkGgiYkBFe1uURt87gQOUAOS
-- commander1: $2b$12$6keEgIodPychAjgGayecW.fBwbUVBcIpu1Ct5o4YydBm9d8qhreHm
-- logistics1: $2b$12$RQf7jNworSoUgBiYok7edeulGKZM9EhqTD2pYrAqO18vTiuZ1Zw.W

-- Example (edit your actual insert statements accordingly):
INSERT INTO user (username, password, role, ...) VALUES
  ('admin', '$2b$12$CfbH8/2DLDrCkHc5Ye7/r.ZvGrgFptkGgiYkBFe1uURt87gQOUAOS', 'ADMIN', ...),
  ('commander1', '$2b$12$6keEgIodPychAjgGayecW.fBwbUVBcIpu1Ct5o4YydBm9d8qhreHm', 'BASE_COMMANDER', ...),
  ('logistics1', '$2b$12$RQf7jNworSoUgBiYok7edeulGKZM9EhqTD2pYrAqO18vTiuZ1Zw.W', 'LOGISTICS_OFFICER', ...);

UPDATE user SET password = '$2a$12$YaTrIqYbDsian6OGDNycrONpNx4ur8Z4dxGB/pt9GsUYAM7BKn3zO' WHERE username = 'logistics1'; 
UPDATE user SET password = '$2a$12$LGSapqbhABCTGiyKXWap6OWcJNGh4LuI6D2yO65rIVH5IPx7ba.hy' WHERE username = 'commander1';