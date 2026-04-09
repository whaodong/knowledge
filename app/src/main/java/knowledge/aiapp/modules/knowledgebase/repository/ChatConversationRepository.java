package knowledge.aiapp.modules.knowledgebase.repository;

import java.util.List;
import java.util.Optional;
import knowledge.aiapp.modules.knowledgebase.domain.ChatConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 知识库会话仓储。
 */
public interface ChatConversationRepository extends JpaRepository<ChatConversationEntity, Long> {

    List<ChatConversationEntity> findByKnowledgeBaseIdAndDeletedFalseOrderByLastMessageAtDesc(Long knowledgeBaseId);

    Optional<ChatConversationEntity> findByIdAndKnowledgeBaseIdAndDeletedFalse(Long id, Long knowledgeBaseId);
}
