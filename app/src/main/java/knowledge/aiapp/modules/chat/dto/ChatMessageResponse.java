package knowledge.aiapp.modules.chat.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 消息响应。
 */
@Getter
@Builder
public class ChatMessageResponse {

    private Long messageId;
    private Long sessionId;
    private String role;
    private String content;
}
