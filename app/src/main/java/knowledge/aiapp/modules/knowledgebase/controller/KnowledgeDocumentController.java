package knowledge.aiapp.modules.knowledgebase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.knowledgebase.dto.request.KnowledgeDocumentUploadRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeChunkResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeDocumentResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeIndexTaskResponse;
import knowledge.aiapp.modules.knowledgebase.service.KnowledgeDocumentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 知识文档接口。
 */
@Tag(name = "知识文档管理")
@RestController
@RequestMapping("/api/knowledgebases/{id}")
public class KnowledgeDocumentController {

    private final KnowledgeDocumentService knowledgeDocumentService;

    public KnowledgeDocumentController(KnowledgeDocumentService knowledgeDocumentService) {
        this.knowledgeDocumentService = knowledgeDocumentService;
    }

    @Operation(summary = "上传知识文档")
    @PostMapping("/documents/upload")
    public Result<KnowledgeDocumentResponse> uploadDocument(@PathVariable("id") Long knowledgeBaseId,
                                                            @Valid @ModelAttribute KnowledgeDocumentUploadRequest request) {
        return Result.success(knowledgeDocumentService.uploadDocument(knowledgeBaseId, request));
    }

    @Operation(summary = "查询文档列表")
    @GetMapping("/documents")
    public Result<List<KnowledgeDocumentResponse>> listDocuments(@PathVariable("id") Long knowledgeBaseId) {
        return Result.success(knowledgeDocumentService.listDocuments(knowledgeBaseId));
    }

    @Operation(summary = "查询文档详情")
    @GetMapping("/documents/{documentId}")
    public Result<KnowledgeDocumentResponse> getDocument(@PathVariable("id") Long knowledgeBaseId,
                                                         @PathVariable Long documentId) {
        return Result.success(knowledgeDocumentService.getDocument(knowledgeBaseId, documentId));
    }

    @Operation(summary = "删除文档")
    @DeleteMapping("/documents/{documentId}")
    public Result<Void> deleteDocument(@PathVariable("id") Long knowledgeBaseId,
                                       @PathVariable Long documentId) {
        knowledgeDocumentService.deleteDocument(knowledgeBaseId, documentId);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "重建文档索引")
    @PostMapping("/documents/{documentId}/reindex")
    public Result<KnowledgeIndexTaskResponse> reindexDocument(@PathVariable("id") Long knowledgeBaseId,
                                                              @PathVariable Long documentId) {
        return Result.success(knowledgeDocumentService.reindexDocument(knowledgeBaseId, documentId));
    }

    @Operation(summary = "查询文档分块")
    @GetMapping("/documents/{documentId}/chunks")
    public Result<List<KnowledgeChunkResponse>> listDocumentChunks(@PathVariable("id") Long knowledgeBaseId,
                                                                   @PathVariable Long documentId) {
        return Result.success(knowledgeDocumentService.listDocumentChunks(knowledgeBaseId, documentId));
    }

    @Operation(summary = "查询知识库任务列表")
    @GetMapping("/tasks")
    public Result<List<KnowledgeIndexTaskResponse>> listTasks(@PathVariable("id") Long knowledgeBaseId) {
        return Result.success(knowledgeDocumentService.listTasks(knowledgeBaseId));
    }

    @Operation(summary = "查询任务详情")
    @GetMapping("/tasks/{taskId}")
    public Result<KnowledgeIndexTaskResponse> getTask(@PathVariable("id") Long knowledgeBaseId,
                                                      @PathVariable Long taskId) {
        return Result.success(knowledgeDocumentService.getTask(knowledgeBaseId, taskId));
    }
}
