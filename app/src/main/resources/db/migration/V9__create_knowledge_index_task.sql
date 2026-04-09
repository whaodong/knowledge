-- 知识索引任务表
CREATE TABLE IF NOT EXISTS knowledge_index_task (
    id BIGSERIAL PRIMARY KEY,
    knowledge_base_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    task_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retry_count INTEGER NOT NULL DEFAULT 3,
    idempotency_key VARCHAR(120) NOT NULL,
    error_message VARCHAR(1000),
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_knowledge_task_kb FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_base(id),
    CONSTRAINT fk_knowledge_task_document FOREIGN KEY (document_id) REFERENCES knowledge_document(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_knowledge_task_idempotency ON knowledge_index_task(idempotency_key);
CREATE INDEX IF NOT EXISTS idx_knowledge_task_kb_status ON knowledge_index_task(knowledge_base_id, status);
CREATE INDEX IF NOT EXISTS idx_knowledge_task_document ON knowledge_index_task(document_id);
