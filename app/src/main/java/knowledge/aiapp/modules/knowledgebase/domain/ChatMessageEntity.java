package knowledge.aiapp.modules.knowledgebase.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knowledge.aiapp.infrastructure.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 知识库对话消息实体。
 */
@Getter
@Setter
@Entity
@Table(name = "chat_message")
public class ChatMessageEntity extends BaseEntity {

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "source_json", columnDefinition = "jsonb")
    private String sourceJson;

    @Column(name = "token_usage_json", columnDefinition = "jsonb")
    private String tokenUsageJson;
}
