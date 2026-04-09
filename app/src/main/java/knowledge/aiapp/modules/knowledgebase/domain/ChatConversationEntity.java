package knowledge.aiapp.modules.knowledgebase.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import knowledge.aiapp.infrastructure.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 知识库对话会话实体。
 */
@Getter
@Setter
@Entity
@Table(name = "chat_conversation")
public class ChatConversationEntity extends BaseEntity {

    @Column(name = "knowledge_base_id", nullable = false)
    private Long knowledgeBaseId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;
}
