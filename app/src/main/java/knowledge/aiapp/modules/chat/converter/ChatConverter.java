package knowledge.aiapp.modules.chat.converter;

import knowledge.aiapp.modules.chat.dto.ChatMessageResponse;
import knowledge.aiapp.modules.chat.dto.CreateChatSessionResponse;
import org.springframework.stereotype.Component;

/**
 * 聊天对象转换器。
 */
@Component
public class ChatConverter {

    public CreateChatSessionResponse toCreateSessionResponse(Long sessionId, String title) {
        return CreateChatSessionResponse.builder()
                .sessionId(sessionId)
                .title(title)
                .build();
    }

    public ChatMessageResponse toMessageResponse(Long messageId, Long sessionId, String role, String content) {
        return ChatMessageResponse.builder()
                .messageId(messageId)
                .sessionId(sessionId)
                .role(role)
                .content(content)
                .build();
    }
}
