package knowledge.aiapp.modules.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import knowledge.aiapp.modules.chat.converter.ChatConverter;
import knowledge.aiapp.modules.chat.dto.ChatMessageRequest;
import knowledge.aiapp.modules.chat.dto.ChatMessageResponse;
import knowledge.aiapp.modules.chat.dto.CreateChatSessionRequest;
import knowledge.aiapp.modules.chat.dto.CreateChatSessionResponse;
import org.springframework.stereotype.Service;

/**
 * 聊天服务默认实现。
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatConverter chatConverter;
    private final AtomicLong sessionIdGenerator = new AtomicLong(1);
    private final AtomicLong messageIdGenerator = new AtomicLong(1);
    private final Map<Long, List<ChatMessageResponse>> sessionMessageStore = new ConcurrentHashMap<>();

    public ChatServiceImpl(ChatConverter chatConverter) {
        this.chatConverter = chatConverter;
    }

    @Override
    public CreateChatSessionResponse createSession(CreateChatSessionRequest request) {
        Long sessionId = sessionIdGenerator.getAndIncrement();
        sessionMessageStore.put(sessionId, new ArrayList<>());
        return chatConverter.toCreateSessionResponse(sessionId, request.getTitle());
    }

    @Override
    public List<ChatMessageResponse> listMessages(Long sessionId) {
        return new ArrayList<>(sessionMessageStore.getOrDefault(sessionId, List.of()));
    }

    @Override
    public ChatMessageResponse sendMessage(Long sessionId, ChatMessageRequest request) {
        ChatMessageResponse messageResponse = chatConverter.toMessageResponse(
                messageIdGenerator.getAndIncrement(),
                sessionId,
                "user",
                request.getContent());
        sessionMessageStore.computeIfAbsent(sessionId, key -> new ArrayList<>()).add(messageResponse);
        return messageResponse;
    }
}
