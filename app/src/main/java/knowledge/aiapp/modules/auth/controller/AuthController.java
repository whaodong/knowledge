package knowledge.aiapp.modules.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import knowledge.aiapp.common.result.Result;
import knowledge.aiapp.modules.auth.dto.request.LoginRequest;
import knowledge.aiapp.modules.auth.dto.response.LoginUserResponse;
import knowledge.aiapp.modules.auth.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 */
@Tag(name = "认证模块")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginUserResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        LoginUserResponse loginUserResponse = authService.login(loginRequest, request, response);
        return Result.success("登录成功", loginUserResponse);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return Result.success("退出成功", null);
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/me")
    public Result<LoginUserResponse> me() {
        return Result.success(authService.currentUser());
    }
}
