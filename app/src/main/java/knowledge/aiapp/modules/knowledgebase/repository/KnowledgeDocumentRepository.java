package knowledge.aiapp.modules.knowledgebase.repository;

import java.util.List;
import java.util.Optional;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 知识文档仓储。
 */
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocumentEntity, Long> {

    List<KnowledgeDocumentEntity> findByKnowledgeBaseIdAndDeletedFalseOrderByCreatedAtDesc(Long knowledgeBaseId);

    Optional<KnowledgeDocumentEntity> findByIdAndKnowledgeBaseIdAndDeletedFalse(Long id, Long knowledgeBaseId);

    long countByKnowledgeBaseIdAndDeletedFalse(Long knowledgeBaseId);

    long countByKnowledgeBaseIdAndStatusAndDeletedFalse(Long knowledgeBaseId, String status);
}
