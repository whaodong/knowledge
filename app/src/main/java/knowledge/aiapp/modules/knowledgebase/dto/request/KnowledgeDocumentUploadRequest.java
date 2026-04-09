package knowledge.aiapp.modules.knowledgebase.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 知识文档上传请求。
 */
@Getter
@Setter
public class KnowledgeDocumentUploadRequest {

    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;
}
