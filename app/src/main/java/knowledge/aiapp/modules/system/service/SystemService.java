package knowledge.aiapp.modules.system.service;

import knowledge.aiapp.modules.system.dto.SystemHealthResponse;

/**
 * 系统服务。
 */
public interface SystemService {

    SystemHealthResponse getHealth();
}
