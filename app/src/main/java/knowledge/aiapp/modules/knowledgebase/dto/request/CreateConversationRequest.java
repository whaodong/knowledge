package knowledge.aiapp.modules.knowledgebase.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建会话请求。
 */
@Getter
@Setter
public class CreateConversationRequest {

    @Size(max = 255, message = "会话标题长度不能超过255")
    private String title;
}
