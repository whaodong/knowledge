package knowledge.aiapp.infrastructure.file;

import java.util.List;
import knowledge.aiapp.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件预处理服务，提供基础校验与占位分片能力。
 */
@Service
public class FilePreprocessService {

    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小超过限制");
        }
    }

    public String detectFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "unknown";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public List<String> splitText(String content) {
        if (!StringUtils.hasText(content)) {
            return List.of();
        }
        return List.of(content);
    }
}
