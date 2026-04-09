package knowledge.aiapp.infrastructure.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import knowledge.aiapp.common.exception.BusinessException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件预处理服务，提供基础校验与占位分片能力。
 */
@Service
public class FilePreprocessService {

    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;
    private static final int CHUNK_SIZE = 800;
    private static final int CHUNK_OVERLAP = 100;
    private static final List<String> SUPPORTED_EXTENSIONS = List.of("pdf", "docx", "md", "markdown");

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小超过限制");
        }
        String extension = detectFileType(file);
        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("不支持的文件类型，仅支持 PDF、DOCX、Markdown");
        }
    }

    public String detectFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "unknown";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * 按文件扩展名提取文本，第一版支持 PDF、DOCX、Markdown。
     */
    public String extractText(Path filePath, String fileExt) {
        if (!StringUtils.hasText(fileExt)) {
            throw new BusinessException("无法识别文档类型");
        }
        String ext = fileExt.toLowerCase();
        try {
            if ("pdf".equals(ext)) {
                return extractPdfText(filePath);
            }
            if ("docx".equals(ext)) {
                return extractDocxText(filePath);
            }
            if ("md".equals(ext) || "markdown".equals(ext)) {
                return Files.readString(filePath, StandardCharsets.UTF_8);
            }
            throw new BusinessException("不支持的文档类型: " + fileExt);
        } catch (IOException ex) {
            throw new BusinessException("文档解析失败: " + ex.getMessage());
        }
    }

    public List<String> splitText(String content) {
        if (!StringUtils.hasText(content)) {
            return List.of();
        }
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < content.length()) {
            int end = Math.min(start + CHUNK_SIZE, content.length());
            String chunk = content.substring(start, end).trim();
            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }
            if (end == content.length()) {
                break;
            }
            start = Math.max(0, end - CHUNK_OVERLAP);
        }
        return chunks;
    }

    private String extractPdfText(Path filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document);
        }
    }

    private String extractDocxText(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder builder = new StringBuilder();
            document.getParagraphs().forEach(paragraph -> builder.append(paragraph.getText()).append('\n'));
            return builder.toString();
        }
    }
}
