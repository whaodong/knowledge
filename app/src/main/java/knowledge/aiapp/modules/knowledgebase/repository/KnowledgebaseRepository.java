package knowledge.aiapp.modules.knowledgebase.repository;

import java.util.List;
import java.util.Optional;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgebaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 知识库仓储。
 */
public interface KnowledgebaseRepository extends JpaRepository<KnowledgebaseEntity, Long> {

    List<KnowledgebaseEntity> findByDeletedFalseOrderByCreatedAtDesc();

    Optional<KnowledgebaseEntity> findByIdAndDeletedFalse(Long id);
}
