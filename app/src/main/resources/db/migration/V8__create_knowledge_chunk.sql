-- 知识分块表
CREATE TABLE IF NOT EXISTS knowledge_chunk (
    id BIGSERIAL PRIMARY KEY,
    knowledge_base_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    chunk_no INTEGER NOT NULL,
    content TEXT NOT NULL,
    content_preview VARCHAR(500),
    token_count INTEGER,
    char_count INTEGER,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    metadata_json JSONB,
    vector_ref_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_knowledge_chunk_kb FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_base(id),
    CONSTRAINT fk_knowledge_chunk_document FOREIGN KEY (document_id) REFERENCES knowledge_document(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_knowledge_chunk_doc_no ON knowledge_chunk(document_id, chunk_no);
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_kb ON knowledge_chunk(knowledge_base_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_enabled ON knowledge_chunk(enabled);
