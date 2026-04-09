package knowledge.aiapp.modules.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import knowledge.aiapp.common.enums.ResultCodeEnum;
import knowledge.aiapp.common.exception.BusinessException;
import knowledge.aiapp.modules.auth.dto.request.LoginRequest;
import knowledge.aiapp.modules.auth.dto.response.LoginUserResponse;
import knowledge.aiapp.modules.auth.service.AuthService;
import knowledge.aiapp.modules.user.domain.User;
import knowledge.aiapp.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

/**
 * 基于 Session 的认证服务实现。
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           SecurityContextRepository securityContextRepository,
                           UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LoginUserResponse login(LoginRequest loginRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            request.getSession(true);
            securityContextRepository.saveContext(securityContext, request, response);

            User user = getUserByUsername(authentication.getName());
            log.info("登录成功, userId={}, username={}", user.getId(), user.getUsername());
            return buildLoginUserResponse(user, authentication);
        } catch (BadCredentialsException ex) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.setClearAuthentication(true);
        logoutHandler.logout(request, response, authentication);
        log.info("退出登录完成");
    }

    @Override
    public LoginUserResponse currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        if ("anonymousUser".equals(authentication.getPrincipal())) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        User user = getUserByUsername(authentication.getName());
        return buildLoginUserResponse(user, authentication);
    }

    private LoginUserResponse buildLoginUserResponse(User user, Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                .collect(Collectors.toList());
        return LoginUserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(roles)
                .build();
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCodeEnum.UNAUTHORIZED));
    }
}
