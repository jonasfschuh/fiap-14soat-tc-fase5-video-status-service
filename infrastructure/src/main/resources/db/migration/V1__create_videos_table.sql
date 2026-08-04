CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS videos (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          VARCHAR(255)  NOT NULL,
    original_filename VARCHAR(500) NOT NULL,
    file_size_bytes  BIGINT        NOT NULL,
    mime_type        VARCHAR(100)  NOT NULL,
    status           VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    storage_key      TEXT,
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_videos_user_id ON videos(user_id);
CREATE INDEX IF NOT EXISTS idx_videos_status  ON videos(status);
