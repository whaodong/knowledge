package knowledge.aiapp.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步执行器配置。
 */
@Configuration
public class AsyncExecutorConfig {

    @Bean("knowledgeSseExecutor")
    public Executor knowledgeSseExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("knowledge-sse-");
        executor.initialize();
        return executor;
    }
}
