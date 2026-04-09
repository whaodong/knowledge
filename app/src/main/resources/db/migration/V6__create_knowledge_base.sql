-- 知识库主表（在历史表基础上补齐生产字段）
ALTER TABLE IF EXISTS knowledge_base
    ADD COLUMN IF NOT EXISTS owner_user_id BIGINT,
    ADD COLUMN IF NOT EXISTS document_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS chunk_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS completed_doc_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS failed_doc_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_indexed_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'knowledge_base'
          AND column_name = 'status'
    ) THEN
        UPDATE knowledge_base
        SET status = CASE
            WHEN status IN ('ENABLED', 'ACTIVE') THEN 'ACTIVE'
            WHEN status = 'DISABLED' THEN 'DISABLED'
            ELSE 'ACTIVE'
        END
        WHERE status IS NOT NULL;
    END IF;
END
$$;

CREATE INDEX IF NOT EXISTS idx_knowledge_base_owner ON knowledge_base(owner_user_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_base_status_deleted ON knowledge_base(status, deleted);
