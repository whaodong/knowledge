package knowledge.aiapp.modules.knowledgebase.service;

import knowledge.aiapp.modules.knowledgebase.converter.KnowledgebaseConverter;
import knowledge.aiapp.modules.knowledgebase.dto.ReindexKnowledgebaseRequest;
import knowledge.aiapp.modules.knowledgebase.dto.ReindexKnowledgebaseResponse;
import org.springframework.stereotype.Service;

/**
 * 知识库服务默认实现。
 */
@Service
public class KnowledgebaseServiceImpl implements KnowledgebaseService {

    private final KnowledgebaseConverter knowledgebaseConverter;

    public KnowledgebaseServiceImpl(KnowledgebaseConverter knowledgebaseConverter) {
        this.knowledgebaseConverter = knowledgebaseConverter;
    }

    @Override
    public ReindexKnowledgebaseResponse reindexByFileId(Long fileId, ReindexKnowledgebaseRequest request) {
        return knowledgebaseConverter.toReindexResponse(fileId, "PROCESSING", "已提交重建索引任务");
    }
}
