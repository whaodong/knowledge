package knowledge.aiapp.modules.knowledgebase.service;

import java.util.List;
import knowledge.aiapp.modules.knowledgebase.dto.request.KnowledgeDocumentUploadRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeChunkResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeDocumentResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeIndexTaskResponse;

/**
 * 知识文档服务。
 */
public interface KnowledgeDocumentService {

    KnowledgeDocumentResponse uploadDocument(Long knowledgeBaseId, KnowledgeDocumentUploadRequest request);

    List<KnowledgeDocumentResponse> listDocuments(Long knowledgeBaseId);

    KnowledgeDocumentResponse getDocument(Long knowledgeBaseId, Long documentId);

    void deleteDocument(Long knowledgeBaseId, Long documentId);

    KnowledgeIndexTaskResponse reindexDocument(Long knowledgeBaseId, Long documentId);

    List<KnowledgeChunkResponse> listDocumentChunks(Long knowledgeBaseId, Long documentId);

    List<KnowledgeIndexTaskResponse> listTasks(Long knowledgeBaseId);

    KnowledgeIndexTaskResponse getTask(Long knowledgeBaseId, Long taskId);
}
