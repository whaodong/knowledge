package knowledge.aiapp.modules.knowledgebase.converter;

import java.util.List;
import knowledge.aiapp.modules.knowledgebase.domain.ChatConversationEntity;
import knowledge.aiapp.modules.knowledgebase.domain.ChatMessageEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeChunkEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeDocumentEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgeIndexTaskEntity;
import knowledge.aiapp.modules.knowledgebase.domain.KnowledgebaseEntity;
import knowledge.aiapp.modules.knowledgebase.dto.response.ChatConversationResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.ChatMessageResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeBaseResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeChunkResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeDocumentResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeIndexTaskResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeSourceResponse;
import org.springframework.stereotype.Component;

/**
 * 知识库对象转换器。
 */
@Component
public class KnowledgebaseConverter {

    public KnowledgeBaseResponse toKnowledgeBaseResponse(KnowledgebaseEntity entity) {
        return KnowledgeBaseResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .ownerUserId(entity.getOwnerUserId())
                .documentCount(entity.getDocumentCount())
                .chunkCount(entity.getChunkCount())
                .completedDocCount(entity.getCompletedDocCount())
                .failedDocCount(entity.getFailedDocCount())
                .lastIndexedAt(entity.getLastIndexedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public KnowledgeDocumentResponse toKnowledgeDocumentResponse(KnowledgeDocumentEntity entity) {
        return KnowledgeDocumentResponse.builder()
                .id(entity.getId())
                .knowledgeBaseId(entity.getKnowledgeBaseId())
                .fileName(entity.getFileName())
                .fileExt(entity.getFileExt())
                .contentType(entity.getContentType())
                .storageKey(entity.getStorageKey())
                .fileSize(entity.getFileSize())
                .status(entity.getStatus())
                .parserType(entity.getParserType())
                .charCount(entity.getCharCount())
                .chunkCount(entity.getChunkCount())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public KnowledgeChunkResponse toKnowledgeChunkResponse(KnowledgeChunkEntity entity) {
        return KnowledgeChunkResponse.builder()
                .id(entity.getId())
                .documentId(entity.getDocumentId())
                .chunkNo(entity.getChunkNo())
                .contentPreview(entity.getContentPreview())
                .tokenCount(entity.getTokenCount())
                .charCount(entity.getCharCount())
                .enabled(entity.getEnabled())
                .build();
    }

    public KnowledgeIndexTaskResponse toKnowledgeIndexTaskResponse(KnowledgeIndexTaskEntity entity) {
        return KnowledgeIndexTaskResponse.builder()
                .id(entity.getId())
                .knowledgeBaseId(entity.getKnowledgeBaseId())
                .documentId(entity.getDocumentId())
                .taskType(entity.getTaskType())
                .status(entity.getStatus())
                .retryCount(entity.getRetryCount())
                .maxRetryCount(entity.getMaxRetryCount())
                .idempotencyKey(entity.getIdempotencyKey())
                .errorMessage(entity.getErrorMessage())
                .startedAt(entity.getStartedAt())
                .finishedAt(entity.getFinishedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ChatConversationResponse toConversationResponse(ChatConversationEntity entity) {
        return ChatConversationResponse.builder()
                .id(entity.getId())
                .knowledgeBaseId(entity.getKnowledgeBaseId())
                .title(entity.getTitle())
                .createdBy(entity.getCreatedBy())
                .lastMessageAt(entity.getLastMessageAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ChatMessageResponse toMessageResponse(ChatMessageEntity entity) {
        return ChatMessageResponse.builder()
                .id(entity.getId())
                .conversationId(entity.getConversationId())
                .role(entity.getRole())
                .content(entity.getContent())
                .sourceJson(entity.getSourceJson())
                .tokenUsageJson(entity.getTokenUsageJson())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public KnowledgeSourceResponse toSourceResponse(Long documentId,
                                                    String documentName,
                                                    Long chunkId,
                                                    Integer chunkNo) {
        return KnowledgeSourceResponse.builder()
                .documentId(documentId)
                .documentName(documentName)
                .chunkId(chunkId)
                .chunkNo(chunkNo)
                .build();
    }

    public String buildConversationTitle(String question) {
        if (question == null || question.isBlank()) {
            return "新会话";
        }
        return question.length() > 24 ? question.substring(0, 24) : question;
    }

    public String buildPreview(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        return content.length() > 120 ? content.substring(0, 120) : content;
    }

    public Integer estimateTokenCount(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }
        // 中文场景按字符数近似估算 token，满足最小版本统计需求
        return Math.max(1, content.length() / 2);
    }

    public String listToJson(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append('"').append(values.get(i).replace("\"", "")).append('"');
        }
        return builder.append(']').toString();
    }
}
