package knowledge.aiapp.modules.system.repository;

import knowledge.aiapp.modules.system.domain.SystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 系统配置仓储。
 */
public interface SystemConfigRepository extends JpaRepository<SystemConfigEntity, Long> {
}
