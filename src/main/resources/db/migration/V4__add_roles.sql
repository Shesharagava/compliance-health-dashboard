INSERT INTO users (username, password, role, created_at)
VALUES 
('admin', '$2a$10$dummyhashedpassword', 'ADMIN', NOW()),
('manager', '$2a$10$dummyhashedpassword', 'MANAGER', NOW())
ON CONFLICT (username) DO NOTHING;