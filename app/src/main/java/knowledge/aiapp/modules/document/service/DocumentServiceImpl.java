package knowledge.aiapp.modules.document.service;

import knowledge.aiapp.common.exception.BusinessException;
import knowledge.aiapp.infrastructure.file.FilePreprocessService;
import knowledge.aiapp.infrastructure.storage.StorageService;
import knowledge.aiapp.modules.document.converter.DocumentConverter;
import knowledge.aiapp.modules.document.dto.DocumentUploadRequest;
import knowledge.aiapp.modules.document.dto.DocumentUploadResponse;
import org.springframework.stereotype.Service;

/**
 * 文档服务默认实现。
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    private final FilePreprocessService filePreprocessService;
    private final StorageService storageService;
    private final DocumentConverter documentConverter;

    public DocumentServiceImpl(FilePreprocessService filePreprocessService,
                               StorageService storageService,
                               DocumentConverter documentConverter) {
        this.filePreprocessService = filePreprocessService;
        this.storageService = storageService;
        this.documentConverter = documentConverter;
    }

    @Override
    public DocumentUploadResponse uploadDocument(DocumentUploadRequest request) {
        try {
            filePreprocessService.validateFile(request.getFile());
            String fileType = filePreprocessService.detectFileType(request.getFile());
            String storagePath = storageService.save(request.getFile(), "documents");
            return documentConverter.toUploadResponse(
                    System.currentTimeMillis(),
                    request.getFile().getOriginalFilename(),
                    fileType,
                    request.getFile().getSize(),
                    storagePath);
        } catch (Exception ex) {
            throw new BusinessException("文档上传失败: " + ex.getMessage());
        }
    }
}
