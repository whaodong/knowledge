package knowledge.aiapp.modules.knowledgebase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 知识库问答请求。
 */
@Getter
@Setter
public class KnowledgeChatRequest {

    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    @NotBlank(message = "问题不能为空")
    private String question;
}
