package knowledge.aiapp.modules.document.converter;

import knowledge.aiapp.modules.document.dto.DocumentUploadResponse;
import org.springframework.stereotype.Component;

/**
 * 文档对象转换器。
 */
@Component
public class DocumentConverter {

    public DocumentUploadResponse toUploadResponse(Long fileId, String fileName, String fileType, Long fileSize,
                                                   String storagePath) {
        return DocumentUploadResponse.builder()
                .fileId(fileId)
                .fileName(fileName)
                .fileType(fileType)
                .fileSize(fileSize)
                .storagePath(storagePath)
                .build();
    }
}
