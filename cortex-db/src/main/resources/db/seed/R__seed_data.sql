-- Repeatable Flyway migration: seed data for local development
-- Uses INSERT ... ON CONFLICT DO NOTHING for idempotency

-- Sample meetings
INSERT INTO meetings (id, title, description, scheduled_at, status, created_at, updated_at)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Sprint Planning Q1', 'Planning session for Q1 sprint goals and task allocation', '2025-01-15 09:00:00+00', 'SCHEDULED', NOW(), NOW()),
    ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Architecture Review', 'Review of the persistence layer design decisions', '2025-01-10 14:00:00+00', 'COMPLETED', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Sample tasks linked to meetings
INSERT INTO tasks (id, meeting_id, title, description, status, priority, due_at, created_at, updated_at)
VALUES
    ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Define database schema', 'Create initial Flyway migration with all tables', 'DONE', 'HIGH', '2025-01-12 17:00:00+00', NOW(), NOW()),
    ('d4e5f6a7-b8c9-0123-defa-234567890123', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Implement repository facades', 'Create persistence facades for all domain aggregates', 'IN_PROGRESS', 'HIGH', '2025-01-14 17:00:00+00', NOW(), NOW()),
    ('e5f6a7b8-c9d0-1234-efab-345678901234', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Write integration tests', 'Add Testcontainers-based integration tests for all facades', 'OPEN', 'MEDIUM', '2025-01-20 17:00:00+00', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Default settings entry
INSERT INTO settings (id, llm_model, temperature, max_tokens, is_default, created_at, updated_at)
VALUES
    ('f6a7b8c9-d0e1-2345-fabc-456789012345', 'llama3.1:8b', 0.1, 4096, TRUE, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Sample log entries
INSERT INTO log_entries (id, meeting_id, content, type, created_at)
VALUES
    ('a7b8c9d0-e1f2-3456-abcd-567890123456', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Discussed trade-offs between JPA and JDBC template. Decided on JPA for developer productivity.', 'NOTE', NOW()),
    ('b8c9d0e1-f2a3-4567-bcde-678901234567', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Action: Johan to finalize the entity mapper design by Friday.', 'ACTION_ITEM', NOW())
ON CONFLICT DO NOTHING;
