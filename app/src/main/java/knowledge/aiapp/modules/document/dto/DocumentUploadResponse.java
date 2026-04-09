package knowledge.aiapp.modules.document.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 文档上传响应。
 */
@Getter
@Builder
public class DocumentUploadResponse {

    private Long fileId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String storagePath;
}
