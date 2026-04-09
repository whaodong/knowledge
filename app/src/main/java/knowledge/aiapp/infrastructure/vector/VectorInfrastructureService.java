package knowledge.aiapp.infrastructure.vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * 向量基础服务占位，后续用于封装向量写入与检索行为。
 */
@Slf4j
@Service
public class VectorInfrastructureService {

    private final ObjectProvider<VectorStore> vectorStoreProvider;

    public VectorInfrastructureService(ObjectProvider<VectorStore> vectorStoreProvider) {
        this.vectorStoreProvider = vectorStoreProvider;
    }

    public String getStoreType() {
        VectorStore vectorStore = vectorStoreProvider.getIfAvailable();
        if (vectorStore == null) {
            return "UNAVAILABLE";
        }
        return vectorStore.getClass().getSimpleName();
    }

    /**
     * 写入向量存储，metadata 需包含知识库与文档上下文。
     */
    public void addChunkDocument(String id, String content, Map<String, Object> metadata) {
        VectorStore vectorStore = vectorStoreProvider.getIfAvailable();
        if (vectorStore == null) {
            log.warn("VectorStore 未启用，跳过向量写入, chunkId={}", id);
            return;
        }
        Map<String, Object> safeMetadata = metadata == null ? new HashMap<>() : new HashMap<>(metadata);
        Document document = new Document(id, content, safeMetadata);
        vectorStore.add(List.of(document));
    }

    /**
     * 语义检索，后续由业务层按 knowledgeBaseId 进行二次过滤。
     */
    public List<Document> similaritySearch(String question, int topK) {
        VectorStore vectorStore = vectorStoreProvider.getIfAvailable();
        if (vectorStore == null) {
            return List.of();
        }
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(topK)
                .build();
        return vectorStore.similaritySearch(request);
    }
}
