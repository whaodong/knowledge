package knowledge.aiapp.modules.system.service;

import knowledge.aiapp.modules.system.converter.SystemConverter;
import knowledge.aiapp.modules.system.dto.SystemHealthResponse;
import org.springframework.stereotype.Service;

/**
 * 系统服务默认实现。
 */
@Service
public class SystemServiceImpl implements SystemService {

    private final SystemConverter systemConverter;

    public SystemServiceImpl(SystemConverter systemConverter) {
        this.systemConverter = systemConverter;
    }

    @Override
    public SystemHealthResponse getHealth() {
        return systemConverter.toHealthResponse("aiapp");
    }
}
