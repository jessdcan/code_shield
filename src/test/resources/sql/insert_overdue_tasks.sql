-- Insert test user
INSERT INTO users (username, email, password) 
VALUES ('dummyuser', 'dummy@test.com', 'dummypassword');

-- Get the ID of the inserted user
SET @user_id = (SELECT id FROM users WHERE username = 'dummyuser');

-- Insert overdue tasks
INSERT INTO task (title, description, status, due_date, created_at, assignee_id)
VALUES 
    ('Overdue Task 1', 'Description 1', 'TODO', DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, @user_id),
    ('Overdue Task 2', 'Description 2', 'IN_PROGRESS', DATEADD('DAY', -2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, @user_id),
    ('Completed Task', 'Description 3', 'COMPLETED', DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, @user_id); 