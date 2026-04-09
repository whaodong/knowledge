package knowledge.aiapp.modules.chat.service;

import java.util.List;
import knowledge.aiapp.modules.chat.dto.ChatMessageRequest;
import knowledge.aiapp.modules.chat.dto.ChatMessageResponse;
import knowledge.aiapp.modules.chat.dto.CreateChatSessionRequest;
import knowledge.aiapp.modules.chat.dto.CreateChatSessionResponse;

/**
 * 聊天服务。
 */
public interface ChatService {

    CreateChatSessionResponse createSession(CreateChatSessionRequest request);

    List<ChatMessageResponse> listMessages(Long sessionId);

    ChatMessageResponse sendMessage(Long sessionId, ChatMessageRequest request);
}
