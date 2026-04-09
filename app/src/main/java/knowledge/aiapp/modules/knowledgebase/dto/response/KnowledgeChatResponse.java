package knowledge.aiapp.modules.knowledgebase.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 知识库问答响应。
 */
@Getter
@Builder
public class KnowledgeChatResponse {

    private Long conversationId;
    private String answer;
    private List<KnowledgeSourceResponse> sources;
}
