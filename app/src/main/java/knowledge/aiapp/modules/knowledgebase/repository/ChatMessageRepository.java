package knowledge.aiapp.modules.knowledgebase.repository;

import java.util.List;
import knowledge.aiapp.modules.knowledgebase.domain.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 知识库消息仓储。
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
