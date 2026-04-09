package knowledge.aiapp.infrastructure.redis;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 知识索引任务生产者。
 */
@Slf4j
@Service
public class KnowledgeTaskStreamProducer {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.knowledge.task.stream-key:knowledge:task:stream}")
    private String streamKey;

    public KnowledgeTaskStreamProducer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendTask(Long taskId) {
        MapRecord<String, String, String> record = MapRecord.create(streamKey, Map.of("taskId", String.valueOf(taskId)));
        redisTemplate.opsForStream().add(record);
        log.info("索引任务已投递到消息流, taskId={}", taskId);
    }
}
