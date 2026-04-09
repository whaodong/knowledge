package knowledge.aiapp.modules.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.system.dto.SystemHealthResponse;
import knowledge.aiapp.modules.system.service.SystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统接口。
 */
@Tag(name = "系统模块")
@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<SystemHealthResponse> health() {
        return Result.success(systemService.getHealth());
    }
}
