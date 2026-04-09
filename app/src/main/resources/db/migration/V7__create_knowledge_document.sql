-- 知识文档表
CREATE TABLE IF NOT EXISTS knowledge_document (
    id BIGSERIAL PRIMARY KEY,
    knowledge_base_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_ext VARCHAR(20) NOT NULL,
    content_type VARCHAR(100),
    storage_key VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    parser_type VARCHAR(30),
    char_count INTEGER,
    chunk_count INTEGER NOT NULL DEFAULT 0,
    error_message VARCHAR(1000),
    uploaded_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_knowledge_document_kb FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_base(id)
);

CREATE INDEX IF NOT EXISTS idx_knowledge_document_kb ON knowledge_document(knowledge_base_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_document_kb_status ON knowledge_document(knowledge_base_id, status, deleted);
