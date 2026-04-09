package knowledge.aiapp.modules.knowledgebase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建知识库请求。
 */
@Getter
@Setter
public class CreateKnowledgeBaseRequest {

    @NotBlank(message = "知识库名称不能为空")
    @Size(max = 255, message = "知识库名称长度不能超过255")
    private String name;

    @Size(max = 1000, message = "知识库描述长度不能超过1000")
    private String description;
}
