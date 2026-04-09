package knowledge.aiapp.modules.system.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import knowledge.aiapp.modules.system.dto.SystemHealthResponse;
import org.springframework.stereotype.Component;

/**
 * 系统对象转换器。
 */
@Component
public class SystemConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public SystemHealthResponse toHealthResponse(String serviceName) {
        return SystemHealthResponse.builder()
                .status("UP")
                .service(serviceName)
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }
}
