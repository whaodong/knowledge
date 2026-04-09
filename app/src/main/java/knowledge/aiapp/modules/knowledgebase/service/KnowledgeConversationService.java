package knowledge.aiapp.modules.knowledgebase.service;

import java.util.List;
import knowledge.aiapp.modules.knowledgebase.dto.request.CreateConversationRequest;
import knowledge.aiapp.modules.knowledgebase.dto.request.KnowledgeChatRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.ChatConversationResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.ChatMessageResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 知识问答会话服务。
 */
public interface KnowledgeConversationService {

    ChatConversationResponse createConversation(Long knowledgeBaseId, CreateConversationRequest request);

    List<ChatConversationResponse> listConversations(Long knowledgeBaseId);

    List<ChatMessageResponse> listMessages(Long knowledgeBaseId, Long conversationId);

    void deleteConversation(Long knowledgeBaseId, Long conversationId);

    KnowledgeChatResponse chat(Long knowledgeBaseId, KnowledgeChatRequest request);

    SseEmitter streamChat(Long knowledgeBaseId, Long conversationId, String question);
}
