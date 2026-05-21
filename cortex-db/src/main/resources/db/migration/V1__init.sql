CREATE TABLE meetings (
    id              UUID PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    scheduled_at    TIMESTAMP WITH TIME ZONE,
    status          VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks (
    id              UUID PRIMARY KEY,
    meeting_id      UUID REFERENCES meetings(id) ON DELETE SET NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    status          VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    priority        VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    due_at          TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE settings (
    id              UUID PRIMARY KEY,
    llm_model       VARCHAR(255) NOT NULL,
    temperature     DOUBLE PRECISION NOT NULL DEFAULT 0.1,
    max_tokens      INTEGER NOT NULL DEFAULT 4096,
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE log_entries (
    id              UUID PRIMARY KEY,
    meeting_id      UUID REFERENCES meetings(id) ON DELETE CASCADE,
    content         TEXT NOT NULL,
    type            VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Indexes for common query patterns
CREATE INDEX idx_tasks_meeting_id ON tasks(meeting_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_log_entries_meeting_id ON log_entries(meeting_id);
CREATE INDEX idx_settings_is_default ON settings(is_default) WHERE is_default = TRUE;
