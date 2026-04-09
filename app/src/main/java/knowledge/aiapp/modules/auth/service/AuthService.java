package knowledge.aiapp.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knowledge.aiapp.modules.auth.dto.request.LoginRequest;
import knowledge.aiapp.modules.auth.dto.response.LoginUserResponse;

/**
 * 认证服务。
 */
public interface AuthService {

    LoginUserResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);

    LoginUserResponse currentUser();
}
