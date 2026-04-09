package knowledge.aiapp.modules.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 发送消息请求。
 */
@Getter
@Setter
public class ChatMessageRequest {

    @NotBlank(message = "消息内容不能为空")
    private String content;
}
