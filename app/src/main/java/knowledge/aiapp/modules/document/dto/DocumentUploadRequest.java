package knowledge.aiapp.modules.document.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档上传请求。
 */
@Getter
@Setter
public class DocumentUploadRequest {

    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;

    private Long knowledgeBaseId;
}
