package knowledge.aiapp.infrastructure.storage;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储抽象接口。
 */
public interface StorageService {

    String save(MultipartFile multipartFile, String directory) throws IOException;
}
