package knowledge.aiapp.modules.knowledgebase.repository;

import java.util.List;
import java.util.Optional;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeIndexTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 知识索引任务仓储。
 */
public interface KnowledgeIndexTaskRepository extends JpaRepository<KnowledgeIndexTaskEntity, Long> {

    Optional<KnowledgeIndexTaskEntity> findByIdAndKnowledgeBaseId(Long id, Long knowledgeBaseId);

    Optional<KnowledgeIndexTaskEntity> findByIdempotencyKey(String idempotencyKey);

    List<KnowledgeIndexTaskEntity> findByKnowledgeBaseIdOrderByCreatedAtDesc(Long knowledgeBaseId);

    long countByKnowledgeBaseIdAndStatus(Long knowledgeBaseId, String status);

    Optional<KnowledgeIndexTaskEntity> findFirstByDocumentIdAndStatusInOrderByCreatedAtDesc(Long documentId,
                                                                                              List<String> statuses);

    List<KnowledgeIndexTaskEntity> findTop20ByStatusOrderByCreatedAtAsc(String status);
}
