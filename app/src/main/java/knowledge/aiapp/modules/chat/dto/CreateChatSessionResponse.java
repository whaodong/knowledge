package knowledge.aiapp.modules.chat.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 创建会话响应。
 */
@Getter
@Builder
public class CreateChatSessionResponse {

    private Long sessionId;
    private String title;
}
