package knowledge.aiapp.modules.knowledgebase.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 问答来源引用响应。
 */
@Getter
@Builder
public class KnowledgeSourceResponse {

    private Long documentId;
    private String documentName;
    private Long chunkId;
    private Integer chunkNo;
}
