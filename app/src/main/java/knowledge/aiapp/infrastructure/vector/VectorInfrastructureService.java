package knowledge.aiapp.infrastructure.vector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
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
}
