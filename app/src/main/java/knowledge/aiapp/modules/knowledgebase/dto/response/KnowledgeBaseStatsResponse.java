package knowledge.aiapp.modules.knowledgebase.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 知识库统计响应。
 */
@Getter
@Builder
public class KnowledgeBaseStatsResponse {

    private Long knowledgeBaseId;
    private Long documentCount;
    private Long chunkCount;
    private Long completedDocCount;
    private Long failedDocCount;
    private Long processingTaskCount;
    private LocalDateTime lastIndexedAt;
}
