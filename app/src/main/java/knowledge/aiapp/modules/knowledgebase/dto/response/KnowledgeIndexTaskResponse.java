package knowledge.aiapp.modules.knowledgebase.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 索引任务响应。
 */
@Getter
@Builder
public class KnowledgeIndexTaskResponse {

    private Long id;
    private Long knowledgeBaseId;
    private Long documentId;
    private String taskType;
    private String status;
    private Integer retryCount;
    private Integer maxRetryCount;
    private String idempotencyKey;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
