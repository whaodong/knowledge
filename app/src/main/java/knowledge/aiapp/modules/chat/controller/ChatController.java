package knowledge.aiapp.modules.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.chat.dto.ChatMessageRequest;
import knowledge.aiapp.modules.chat.dto.ChatMessageResponse;
import knowledge.aiapp.modules.chat.dto.CreateChatSessionRequest;
import knowledge.aiapp.modules.chat.dto.CreateChatSessionResponse;
import knowledge.aiapp.modules.chat.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天接口。
 */
@Tag(name = "聊天模块")
@RestController
@RequestMapping("/api/chat/sessions")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "创建会话")
    @PostMapping
    public Result<CreateChatSessionResponse> createSession(@Valid @RequestBody CreateChatSessionRequest request) {
        return Result.success(chatService.createSession(request));
    }

    @Operation(summary = "获取会话消息")
    @GetMapping("/{sessionId}/messages")
    public Result<List<ChatMessageResponse>> listMessages(@PathVariable Long sessionId) {
        return Result.success(chatService.listMessages(sessionId));
    }

    @Operation(summary = "发送会话消息")
    @PostMapping("/{sessionId}/messages")
    public Result<ChatMessageResponse> sendMessage(@PathVariable Long sessionId,
                                                   @Valid @RequestBody ChatMessageRequest request) {
        return Result.success(chatService.sendMessage(sessionId, request));
    }
}
