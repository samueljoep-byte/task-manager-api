CREATE INDEX idx_task_user_id
ON task(user_id);

CREATE INDEX idx_task_status_user
ON task(status, user_id);