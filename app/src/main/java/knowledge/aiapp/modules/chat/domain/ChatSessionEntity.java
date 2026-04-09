package knowledge.aiapp.modules.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knowledge.aiapp.infrastructure.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 聊天会话实体。
 */
@Getter
@Setter
@Entity
@Table(name = "chat_session")
public class ChatSessionEntity extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
