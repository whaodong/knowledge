package knowledge.aiapp.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 审计配置，自动维护实体审计时间字段。
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
