package knowledge.aiapp.modules.knowledgebase.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 知识分块响应。
 */
@Getter
@Builder
public class KnowledgeChunkResponse {

    private Long id;
    private Long documentId;
    private Integer chunkNo;
    private String contentPreview;
    private Integer tokenCount;
    private Integer charCount;
    private Boolean enabled;
}
