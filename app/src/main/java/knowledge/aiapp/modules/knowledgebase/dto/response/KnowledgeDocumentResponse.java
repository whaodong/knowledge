package knowledge.aiapp.modules.knowledgebase.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 知识文档响应。
 */
@Getter
@Builder
public class KnowledgeDocumentResponse {

    private Long id;
    private Long knowledgeBaseId;
    private String fileName;
    private String fileExt;
    private String contentType;
    private String storageKey;
    private Long fileSize;
    private String status;
    private String parserType;
    private Integer charCount;
    private Integer chunkCount;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
