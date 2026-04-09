package knowledge.aiapp.modules.knowledgebase.repository;

import java.util.List;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 知识分块仓储。
 */
public interface KnowledgeChunkRepository extends JpaRepository<KnowledgeChunkEntity, Long> {

    List<KnowledgeChunkEntity> findByDocumentIdOrderByChunkNoAsc(Long documentId);

    List<KnowledgeChunkEntity> findTop10ByKnowledgeBaseIdAndEnabledTrueOrderByIdDesc(Long knowledgeBaseId);

    long countByKnowledgeBaseId(Long knowledgeBaseId);

    void deleteByDocumentId(Long documentId);
}
