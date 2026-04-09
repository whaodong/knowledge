package knowledge.aiapp.modules.knowledgebase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.knowledgebase.dto.request.CreateKnowledgeBaseRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeBaseResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeBaseStatsResponse;
import knowledge.aiapp.modules.knowledgebase.service.KnowledgebaseService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 知识库管理接口。
 */
@Tag(name = "知识库管理")
@RestController
@RequestMapping("/api/knowledgebases")
public class KnowledgebaseController {

    private final KnowledgebaseService knowledgebaseService;

    public KnowledgebaseController(KnowledgebaseService knowledgebaseService) {
        this.knowledgebaseService = knowledgebaseService;
    }

    @Operation(summary = "创建知识库")
    @PostMapping
    public Result<KnowledgeBaseResponse> createKnowledgeBase(@Valid @RequestBody CreateKnowledgeBaseRequest request) {
        return Result.success(knowledgebaseService.createKnowledgeBase(request));
    }

    @Operation(summary = "查询知识库列表")
    @GetMapping
    public Result<List<KnowledgeBaseResponse>> listKnowledgeBases() {
        return Result.success(knowledgebaseService.listKnowledgeBases());
    }

    @Operation(summary = "查询知识库详情")
    @GetMapping("/{id}")
    public Result<KnowledgeBaseResponse> getKnowledgeBase(@PathVariable("id") Long knowledgeBaseId) {
        return Result.success(knowledgebaseService.getKnowledgeBase(knowledgeBaseId));
    }

    @Operation(summary = "删除知识库")
    @DeleteMapping("/{id}")
    public Result<Void> deleteKnowledgeBase(@PathVariable("id") Long knowledgeBaseId) {
        knowledgebaseService.deleteKnowledgeBase(knowledgeBaseId);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "获取知识库统计")
    @GetMapping("/{id}/stats")
    public Result<KnowledgeBaseStatsResponse> getKnowledgeBaseStats(@PathVariable("id") Long knowledgeBaseId) {
        return Result.success(knowledgebaseService.getKnowledgeBaseStats(knowledgeBaseId));
    }
}
