package knowledge.aiapp.infrastructure.redis;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import knowledge.aiapp.modules.knowledgebase.constant.KnowledgeConstants;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeIndexTaskEntity;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeIndexTaskRepository;
import knowledge.aiapp.modules.knowledgebase.service.KnowledgeIndexTaskProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 知识索引任务消费者。
 */
@Slf4j
@Service
public class KnowledgeTaskStreamConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final KnowledgeIndexTaskProcessor taskProcessor;
    private final KnowledgeIndexTaskRepository taskRepository;

    @Value("${app.knowledge.task.stream-key:knowledge:task:stream}")
    private String streamKey;

    @Value("${app.knowledge.task.group:knowledge-task-group}")
    private String group;

    @Value("${app.knowledge.task.consumer:knowledge-task-consumer-1}")
    private String consumerName;

    public KnowledgeTaskStreamConsumer(RedisTemplate<String, Object> redisTemplate,
                                       KnowledgeIndexTaskProcessor taskProcessor,
                                       KnowledgeIndexTaskRepository taskRepository) {
        this.redisTemplate = redisTemplate;
        this.taskProcessor = taskProcessor;
        this.taskRepository = taskRepository;
    }

    @Scheduled(fixedDelayString = "${app.knowledge.task.poll-interval-ms:3000}")
    public void consume() {
        ensureConsumerGroup();
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                Consumer.from(group, consumerName),
                StreamReadOptions.empty().count(5).block(Duration.ofSeconds(1)),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()));

        if (records != null && !records.isEmpty()) {
            for (MapRecord<String, Object, Object> record : records) {
                processRecord(record);
            }
        }
        compensatePendingTasks();
    }

    private void processRecord(MapRecord<String, Object, Object> record) {
        try {
            Map<Object, Object> value = record.getValue();
            Long taskId = Long.valueOf(String.valueOf(value.get("taskId")));
            taskProcessor.processTask(taskId);
            redisTemplate.opsForStream().acknowledge(streamKey, group, record.getId());
        } catch (Exception ex) {
            log.error("处理索引任务消息失败, recordId={}", record.getId(), ex);
        }
    }

    private void ensureConsumerGroup() {
        try {
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), group);
        } catch (RedisSystemException ex) {
            // 分组已存在时忽略
        } catch (Exception ex) {
            log.debug("创建消费者组跳过: {}", ex.getMessage());
        }
    }

    private void compensatePendingTasks() {
        List<KnowledgeIndexTaskEntity> pendingTasks = taskRepository.findTop20ByStatusOrderByCreatedAtAsc(
                KnowledgeConstants.TASK_STATUS_PENDING);
        for (KnowledgeIndexTaskEntity task : pendingTasks) {
            try {
                taskProcessor.processTask(task.getId());
            } catch (Exception ex) {
                log.error("补偿处理待执行任务失败, taskId={}", task.getId(), ex);
            }
        }
    }
}
