package knowledge.aiapp.modules.document.service;

import knowledge.aiapp.modules.document.dto.DocumentUploadRequest;
import knowledge.aiapp.modules.document.dto.DocumentUploadResponse;

/**
 * 文档服务。
 */
public interface DocumentService {

    DocumentUploadResponse uploadDocument(DocumentUploadRequest request);
}
