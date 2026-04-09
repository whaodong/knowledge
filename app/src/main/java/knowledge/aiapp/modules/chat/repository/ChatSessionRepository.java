package knowledge.aiapp.modules.chat.repository;

import knowledge.aiapp.modules.chat.domain.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 聊天会话仓储。
 */
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, Long> {
}
