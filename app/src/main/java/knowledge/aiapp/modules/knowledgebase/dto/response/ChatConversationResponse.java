package knowledge.aiapp.modules.knowledgebase.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 会话响应。
 */
@Getter
@Builder
public class ChatConversationResponse {

    private Long id;
    private Long knowledgeBaseId;
    private String title;
    private Long createdBy;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
}
