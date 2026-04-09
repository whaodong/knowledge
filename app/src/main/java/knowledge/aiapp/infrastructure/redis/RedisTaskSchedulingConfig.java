package knowledge.aiapp.infrastructure.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启用任务调度，驱动 Redis Stream 消费。
 */
@Configuration
@EnableScheduling
public class RedisTaskSchedulingConfig {
}
