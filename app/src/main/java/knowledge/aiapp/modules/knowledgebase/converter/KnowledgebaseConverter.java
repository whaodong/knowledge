package knowledge.aiapp.modules.knowledgebase.converter;

import knowledge.aiapp.modules.knowledgebase.dto.ReindexKnowledgebaseResponse;
import org.springframework.stereotype.Component;

/**
 * 知识库对象转换器。
 */
@Component
public class KnowledgebaseConverter {

    public ReindexKnowledgebaseResponse toReindexResponse(Long fileId, String status, String message) {
        return ReindexKnowledgebaseResponse.builder()
                .fileId(fileId)
                .status(status)
                .message(message)
                .build();
    }
}
