package knowledge.aiapp.infrastructure.ai;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * Prompt 模板读取服务，统一从 resources/prompts 加载模板。
 */
@Service
public class PromptTemplateService {

    private final ResourceLoader resourceLoader;

    public PromptTemplateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String loadPrompt(String relativePath) {
        Resource resource = resourceLoader.getResource("classpath:prompts/" + relativePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("读取 Prompt 模板失败: " + relativePath, ex);
        }
    }
}
