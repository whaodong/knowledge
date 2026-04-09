package knowledge.aiapp.modules.knowledgebase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import knowledge.aiapp.common.enums.ResultCodeEnum;
import knowledge.aiapp.common.exception.BusinessException;
import knowledge.aiapp.infrastructure.ai.PromptTemplateService;
import knowledge.aiapp.infrastructure.vector.VectorInfrastructureService;
import knowledge.aiapp.modules.knowledgebase.constant.KnowledgeConstants;
import knowledge.aiapp.modules.knowledgebase.converter.KnowledgebaseConverter;
import knowledge.aiapp.modules.knowledgebase.domain.ChatConversationEntity;
import knowledge.aiapp.modules.knowledgebase.domain.ChatMessageEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeChunkEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeDocumentEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgebaseEntity;
import knowledge.aiapp.modules.knowledgebase.dto.request.CreateConversationRequest;
import knowledge.aiapp.modules.knowledgebase.dto.request.KnowledgeChatRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.ChatConversationResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.ChatMessageResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeChatResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeSourceResponse;
import knowledge.aiapp.modules.knowledgebase.repository.ChatConversationRepository;
import knowledge.aiapp.modules.knowledgebase.repository.ChatMessageRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeChunkRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgeDocumentRepository;
import knowledge.aiapp.modules.knowledgebase.repository.KnowledgebaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 知识问答会话服务实现。
 */
@Slf4j
@Service
public class KnowledgeConversationServiceImpl implements KnowledgeConversationService {

    private final KnowledgebaseRepository knowledgebaseRepository;
    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final KnowledgeChunkRepository chunkRepository;
    private final KnowledgeDocumentRepository documentRepository;
    private final KnowledgebaseConverter knowledgebaseConverter;
    private final VectorInfrastructureService vectorInfrastructureService;
    private final PromptTemplateService promptTemplateService;
    private final ObjectProvider<ChatClient> chatClientProvider;
    private final ObjectMapper objectMapper;
    private final java.util.concurrent.Executor knowledgeSseExecutor;

    public KnowledgeConversationServiceImpl(KnowledgebaseRepository knowledgebaseRepository,
                                            ChatConversationRepository conversationRepository,
                                            ChatMessageRepository messageRepository,
                                            KnowledgeChunkRepository chunkRepository,
                                            KnowledgeDocumentRepository documentRepository,
                                            KnowledgebaseConverter knowledgebaseConverter,
                                            VectorInfrastructureService vectorInfrastructureService,
                                            PromptTemplateService promptTemplateService,
                                            ObjectProvider<ChatClient> chatClientProvider,
                                            ObjectMapper objectMapper,
                                            @Qualifier("knowledgeSseExecutor") java.util.concurrent.Executor knowledgeSseExecutor) {
        this.knowledgebaseRepository = knowledgebaseRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.chunkRepository = chunkRepository;
        this.documentRepository = documentRepository;
        this.knowledgebaseConverter = knowledgebaseConverter;
        this.vectorInfrastructureService = vectorInfrastructureService;
        this.promptTemplateService = promptTemplateService;
        this.chatClientProvider = chatClientProvider;
        this.objectMapper = objectMapper;
        this.knowledgeSseExecutor = knowledgeSseExecutor;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatConversationResponse createConversation(Long knowledgeBaseId, CreateConversationRequest request) {
        getKnowledgeBase(knowledgeBaseId);
        ChatConversationEntity conversation = new ChatConversationEntity();
        conversation.setKnowledgeBaseId(knowledgeBaseId);
        conversation.setTitle(request == null || request.getTitle() == null || request.getTitle().isBlank()
                ? "新会话"
                : request.getTitle());
        conversation.setCreatedBy(1L);
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setDeleted(false);
        ChatConversationEntity saved = conversationRepository.save(conversation);
        return knowledgebaseConverter.toConversationResponse(saved);
    }

    @Override
    public List<ChatConversationResponse> listConversations(Long knowledgeBaseId) {
        getKnowledgeBase(knowledgeBaseId);
        return conversationRepository.findByKnowledgeBaseIdAndDeletedFalseOrderByLastMessageAtDesc(knowledgeBaseId).stream()
                .map(knowledgebaseConverter::toConversationResponse)
                .toList();
    }

    @Override
    public List<ChatMessageResponse> listMessages(Long knowledgeBaseId, Long conversationId) {
        getConversation(knowledgeBaseId, conversationId);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(knowledgebaseConverter::toMessageResponse)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(Long knowledgeBaseId, Long conversationId) {
        ChatConversationEntity conversation = getConversation(knowledgeBaseId, conversationId);
        conversation.setDeleted(true);
        conversationRepository.save(conversation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeChatResponse chat(Long knowledgeBaseId, KnowledgeChatRequest request) {
        ChatConversationEntity conversation = getConversation(knowledgeBaseId, request.getConversationId());
        RagResult ragResult = generateAnswer(knowledgeBaseId, request.getQuestion());

        persistUserAndAssistantMessages(conversation, request.getQuestion(), ragResult.answer(), ragResult.sources());
        return KnowledgeChatResponse.builder()
                .conversationId(conversation.getId())
                .answer(ragResult.answer())
                .sources(ragResult.sources())
                .build();
    }

    @Override
    public SseEmitter streamChat(Long knowledgeBaseId, Long conversationId, String question) {
        ChatConversationEntity conversation = getConversation(knowledgeBaseId, conversationId);
        SseEmitter emitter = new SseEmitter(120_000L);
        knowledgeSseExecutor.execute(() -> {
            try {
                RagResult ragResult = generateAnswer(knowledgeBaseId, question);
                emitter.send(SseEmitter.event().name("start").data(Map.of("conversationId", conversationId)));
                for (String part : splitForStream(ragResult.answer())) {
                    emitter.send(SseEmitter.event().name("chunk").data(Map.of("content", part)));
                }
                emitter.send(SseEmitter.event().name("end").data(Map.of("sources", ragResult.sources())));
                persistUserAndAssistantMessages(conversation, question, ragResult.answer(), ragResult.sources());
                emitter.complete();
            } catch (Exception ex) {
                try {
                    emitter.send(SseEmitter.event().name("error").data(Map.of("message", ex.getMessage())));
                } catch (IOException ioException) {
                    log.error("SSE 错误事件发送失败", ioException);
                }
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    private RagResult generateAnswer(Long knowledgeBaseId, String question) {
        List<KnowledgeSourceResponse> sources = new ArrayList<>();
        String context = buildContextByVectorSearch(knowledgeBaseId, question, sources);
        if (context.isBlank()) {
            context = "未命中可用知识片段。";
        }
        String answer = callModelOrFallback(question, context);
        return new RagResult(answer, sources);
    }

    private String buildContextByVectorSearch(Long knowledgeBaseId,
                                              String question,
                                              List<KnowledgeSourceResponse> sources) {
        List<Document> documents = vectorInfrastructureService.similaritySearch(question, 6);
        StringBuilder contextBuilder = new StringBuilder();

        for (Document document : documents) {
            Map<String, Object> metadata = document.getMetadata();
            Long metadataKbId = toLong(metadata.get("knowledgeBaseId"));
            if (metadataKbId == null || !knowledgeBaseId.equals(metadataKbId)) {
                continue;
            }
            Long chunkId = toLong(metadata.get("chunkId"));
            Long documentId = toLong(metadata.get("documentId"));
            Integer chunkNo = toInteger(metadata.get("chunkNo"));
            String documentName = metadata.get("documentName") == null ? "" : String.valueOf(metadata.get("documentName"));
            if (chunkId != null && documentId != null) {
                sources.add(knowledgebaseConverter.toSourceResponse(documentId, documentName, chunkId, chunkNo));
            }
            contextBuilder.append("- ").append(document.getText()).append('\n');
        }

        if (contextBuilder.isEmpty()) {
            List<KnowledgeChunkEntity> fallbackChunks = chunkRepository
                    .findTop10ByKnowledgeBaseIdAndEnabledTrueOrderByIdDesc(knowledgeBaseId);
            for (KnowledgeChunkEntity chunk : fallbackChunks) {
                KnowledgeDocumentEntity document = documentRepository.findById(chunk.getDocumentId()).orElse(null);
                sources.add(knowledgebaseConverter.toSourceResponse(
                        chunk.getDocumentId(),
                        document == null ? "" : document.getFileName(),
                        chunk.getId(),
                        chunk.getChunkNo()));
                contextBuilder.append("- ").append(chunk.getContent()).append('\n');
            }
        }
        return contextBuilder.toString();
    }

    private String callModelOrFallback(String question, String context) {
        ChatClient chatClient = chatClientProvider.getIfAvailable();
        if (chatClient == null) {
            return "基于当前知识库检索结果，问题【" + question + "】的参考答案如下：\n" + context;
        }
        String promptTemplate = promptTemplateService.loadPrompt("chat/rag-answer.st");
        String prompt = promptTemplate
                .replace("{question}", question)
                .replace("{{question}}", question)
                .replace("{context}", context)
                .replace("{{context}}", context);
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    private void persistUserAndAssistantMessages(ChatConversationEntity conversation,
                                                 String question,
                                                 String answer,
                                                 List<KnowledgeSourceResponse> sources) {
        ChatMessageEntity userMessage = new ChatMessageEntity();
        userMessage.setConversationId(conversation.getId());
        userMessage.setRole(KnowledgeConstants.MESSAGE_ROLE_USER);
        userMessage.setContent(question);
        messageRepository.save(userMessage);

        ChatMessageEntity assistantMessage = new ChatMessageEntity();
        assistantMessage.setConversationId(conversation.getId());
        assistantMessage.setRole(KnowledgeConstants.MESSAGE_ROLE_ASSISTANT);
        assistantMessage.setContent(answer);
        assistantMessage.setSourceJson(toJson(sources));
        messageRepository.save(assistantMessage);

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    private ChatConversationEntity getConversation(Long knowledgeBaseId, Long conversationId) {
        getKnowledgeBase(knowledgeBaseId);
        return conversationRepository.findByIdAndKnowledgeBaseIdAndDeletedFalse(conversationId, knowledgeBaseId)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "会话不存在"));
    }

    private KnowledgebaseEntity getKnowledgeBase(Long knowledgeBaseId) {
        return knowledgebaseRepository.findByIdAndDeletedFalse(knowledgeBaseId)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.NOT_FOUND.getCode(), "知识库不存在"));
    }

    private List<String> splitForStream(String answer) {
        List<String> parts = new ArrayList<>();
        int cursor = 0;
        while (cursor < answer.length()) {
            int end = Math.min(cursor + 12, answer.length());
            parts.add(answer.substring(cursor, end));
            cursor = end;
        }
        return parts;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return "[]";
        }
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (Exception ex) {
            return null;
        }
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(String.valueOf(value));
        } catch (Exception ex) {
            return null;
        }
    }

    private record RagResult(String answer, List<KnowledgeSourceResponse> sources) {
    }
}
