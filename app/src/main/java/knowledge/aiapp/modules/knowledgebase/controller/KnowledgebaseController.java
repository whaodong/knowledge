package knowledge.aiapp.modules.knowledgebase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.knowledgebase.dto.ReindexKnowledgebaseRequest;
import knowledge.aiapp.modules.knowledgebase.dto.ReindexKnowledgebaseResponse;
import knowledge.aiapp.modules.knowledgebase.service.KnowledgebaseService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 知识库接口。
 */
@Tag(name = "知识库模块")
@RestController
@RequestMapping("/api/knowledgebases")
public class KnowledgebaseController {

    private final KnowledgebaseService knowledgebaseService;

    public KnowledgebaseController(KnowledgebaseService knowledgebaseService) {
        this.knowledgebaseService = knowledgebaseService;
    }

    @Operation(summary = "根据文件重建知识库索引")
    @PostMapping("/reindex/{fileId}")
    public Result<ReindexKnowledgebaseResponse> reindex(@PathVariable Long fileId,
                                                        @RequestBody(required = false)
                                                        ReindexKnowledgebaseRequest request) {
        ReindexKnowledgebaseRequest realRequest = request == null ? new ReindexKnowledgebaseRequest() : request;
        return Result.success(knowledgebaseService.reindexByFileId(fileId, realRequest));
    }
}
