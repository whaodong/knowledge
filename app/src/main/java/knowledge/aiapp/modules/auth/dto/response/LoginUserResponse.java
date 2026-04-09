package knowledge.aiapp.modules.auth.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 登录用户信息响应。
 */
@Getter
@Builder
public class LoginUserResponse {

    private Long userId;
    private String username;
    private String nickname;
    private List<String> roles;
}
