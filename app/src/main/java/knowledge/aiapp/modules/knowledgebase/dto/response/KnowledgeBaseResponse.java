package knowledge.aiapp.modules.knowledgebase.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 知识库响应。
 */
@Getter
@Builder
public class KnowledgeBaseResponse {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long ownerUserId;
    private Integer documentCount;
    private Integer chunkCount;
    private Integer completedDocCount;
    private Integer failedDocCount;
    private LocalDateTime lastIndexedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
