package knowledge.aiapp.modules.knowledgebase.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 知识库重建索引响应。
 */
@Getter
@Builder
public class ReindexKnowledgebaseResponse {

    private Long fileId;
    private String status;
    private String message;
}
