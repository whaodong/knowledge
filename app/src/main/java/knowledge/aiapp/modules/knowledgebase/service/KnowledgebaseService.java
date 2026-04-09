package knowledge.aiapp.modules.knowledgebase.service;

import java.util.List;
import knowledge.aiapp.modules.knowledgebase.dto.request.CreateKnowledgeBaseRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeBaseResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeBaseStatsResponse;

/**
 * 知识库管理服务。
 */
public interface KnowledgebaseService {

    KnowledgeBaseResponse createKnowledgeBase(CreateKnowledgeBaseRequest request);

    List<KnowledgeBaseResponse> listKnowledgeBases();

    KnowledgeBaseResponse getKnowledgeBase(Long knowledgeBaseId);

    void deleteKnowledgeBase(Long knowledgeBaseId);

    KnowledgeBaseStatsResponse getKnowledgeBaseStats(Long knowledgeBaseId);

    void refreshKnowledgeBaseStats(Long knowledgeBaseId);
}
