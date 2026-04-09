package knowledge.aiapp.modules.document.repository;

import knowledge.aiapp.modules.document.domain.DocumentFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文档仓储。
 */
public interface DocumentFileRepository extends JpaRepository<DocumentFileEntity, Long> {
}
