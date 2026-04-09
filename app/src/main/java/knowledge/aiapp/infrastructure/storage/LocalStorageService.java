package knowledge.aiapp.infrastructure.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 本地文件存储实现。
 */
@Service
public class LocalStorageService implements StorageService {

    private final Path basePath;

    public LocalStorageService(@Value("${app.storage.local.base-path:./data/storage}") String basePath) throws IOException {
        this.basePath = Path.of(basePath).toAbsolutePath().normalize();
        Files.createDirectories(this.basePath);
    }

    @Override
    public String save(MultipartFile multipartFile, String directory) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String filename = UUID.randomUUID() + "-" + (StringUtils.hasText(originalFilename) ? originalFilename : "unknown.bin");
        Path targetDirectory = this.basePath.resolve(directory).normalize();
        Files.createDirectories(targetDirectory);
        Path targetFile = targetDirectory.resolve(filename).normalize();
        Files.copy(multipartFile.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        return targetFile.toString();
    }
}
