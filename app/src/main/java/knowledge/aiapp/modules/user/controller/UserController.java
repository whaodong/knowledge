package knowledge.aiapp.modules.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.user.dto.UserProfileResponse;
import knowledge.aiapp.modules.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口。
 */
@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取用户资料")
    @GetMapping("/{userId}")
    public Result<UserProfileResponse> getProfile(@PathVariable Long userId) {
        return Result.success(userService.getProfile(userId));
    }
}
