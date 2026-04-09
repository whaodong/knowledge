package knowledge.aiapp.modules.knowledgebase.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 知识库重建索引请求。
 */
@Getter
@Setter
public class ReindexKnowledgebaseRequest {

    private String trigger;
}
