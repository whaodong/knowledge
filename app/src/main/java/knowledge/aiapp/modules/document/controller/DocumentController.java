package knowledge.aiapp.modules.document.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.document.dto.DocumentUploadRequest;
import knowledge.aiapp.modules.document.dto.DocumentUploadResponse;
import knowledge.aiapp.modules.document.service.DocumentService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文档接口。
 */
@Tag(name = "文档模块")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary = "上传文档")
    @PostMapping("/upload")
    public Result<DocumentUploadResponse> upload(@Valid @ModelAttribute DocumentUploadRequest request) {
        return Result.success(documentService.uploadDocument(request));
    }
}
