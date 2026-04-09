package knowledge.aiapp.modules.knowledgebase.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 会话消息响应。
 */
@Getter
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long conversationId;
    private String role;
    private String content;
    private String sourceJson;
    private String tokenUsageJson;
    private LocalDateTime createdAt;
}
