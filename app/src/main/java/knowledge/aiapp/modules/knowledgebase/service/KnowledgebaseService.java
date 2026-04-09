package knowledge.aiapp.modules.knowledgebase.service;

import knowledge.aiapp.modules.knowledgebase.dto.ReindexKnowledgebaseRequest;
import knowledge.aiapp.modules.knowledgebase.dto.ReindexKnowledgebaseResponse;

/**
 * 知识库服务。
 */
public interface KnowledgebaseService {

    ReindexKnowledgebaseResponse reindexByFileId(Long fileId, ReindexKnowledgebaseRequest request);
}
