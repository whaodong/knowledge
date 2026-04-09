package knowledge.aiapp.modules.system.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 系统健康响应。
 */
@Getter
@Builder
public class SystemHealthResponse {

    private String status;
    private String service;
    private String timestamp;
}
