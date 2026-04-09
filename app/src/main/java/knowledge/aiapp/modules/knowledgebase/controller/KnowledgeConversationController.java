package knowledge.aiapp.modules.knowledgebase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.knowledgebase.dto.request.CreateConversationRequest;
import knowledge.aiapp.modules.knowledgebase.dto.request.KnowledgeChatRequest;
import knowledge.aiapp.modules.knowledgebase.dto.response.ChatConversationResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.ChatMessageResponse;
import knowledge.aiapp.modules.knowledgebase.dto.response.KnowledgeChatResponse;
import knowledge.aiapp.modules.knowledgebase.service.KnowledgeConversationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 知识问答会话接口。
 */
@Tag(name = "知识问答")
@RestController
@RequestMapping("/api/knowledgebases/{id}")
public class KnowledgeConversationController {

    private final KnowledgeConversationService conversationService;

    public KnowledgeConversationController(KnowledgeConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @Operation(summary = "创建会话")
    @PostMapping("/conversations")
    public Result<ChatConversationResponse> createConversation(@PathVariable("id") Long knowledgeBaseId,
                                                               @RequestBody(required = false) CreateConversationRequest request) {
        CreateConversationRequest realRequest = request == null ? new CreateConversationRequest() : request;
        return Result.success(conversationService.createConversation(knowledgeBaseId, realRequest));
    }

    @Operation(summary = "查询会话列表")
    @GetMapping("/conversations")
    public Result<List<ChatConversationResponse>> listConversations(@PathVariable("id") Long knowledgeBaseId) {
        return Result.success(conversationService.listConversations(knowledgeBaseId));
    }

    @Operation(summary = "查询会话消息")
    @GetMapping("/conversations/{conversationId}/messages")
    public Result<List<ChatMessageResponse>> listMessages(@PathVariable("id") Long knowledgeBaseId,
                                                          @PathVariable Long conversationId) {
        return Result.success(conversationService.listMessages(knowledgeBaseId, conversationId));
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/conversations/{conversationId}")
    public Result<Void> deleteConversation(@PathVariable("id") Long knowledgeBaseId,
                                           @PathVariable Long conversationId) {
        conversationService.deleteConversation(knowledgeBaseId, conversationId);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "知识库问答")
    @PostMapping("/chat")
    public Result<KnowledgeChatResponse> chat(@PathVariable("id") Long knowledgeBaseId,
                                              @Valid @RequestBody KnowledgeChatRequest request) {
        return Result.success(conversationService.chat(knowledgeBaseId, request));
    }

    @Operation(summary = "知识库问答流式输出")
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@PathVariable("id") Long knowledgeBaseId,
                                 @RequestParam Long conversationId,
                                 @RequestParam String question) {
        return conversationService.streamChat(knowledgeBaseId, conversationId, question);
    }
}
