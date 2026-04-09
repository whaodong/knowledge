package knowledge.aiapp.modules.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建会话请求。
 */
@Getter
@Setter
public class CreateChatSessionRequest {

    @NotBlank(message = "会话标题不能为空")
    private String title;
}
