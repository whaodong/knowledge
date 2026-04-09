-- 知识库会话表
CREATE TABLE IF NOT EXISTS chat_conversation (
    id BIGSERIAL PRIMARY KEY,
    knowledge_base_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_by BIGINT,
    last_message_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_chat_conversation_kb FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_base(id)
);

CREATE INDEX IF NOT EXISTS idx_chat_conversation_kb ON chat_conversation(knowledge_base_id, deleted);
CREATE INDEX IF NOT EXISTS idx_chat_conversation_last_message ON chat_conversation(last_message_at);
