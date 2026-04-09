package knowledge.aiapp.modules.user.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 用户资料响应。
 */
@Getter
@Builder
public class UserProfileResponse {

    private Long userId;
    private String username;
}
