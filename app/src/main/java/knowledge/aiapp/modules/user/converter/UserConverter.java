package knowledge.aiapp.modules.user.converter;

import knowledge.aiapp.modules.user.dto.UserProfileResponse;
import org.springframework.stereotype.Component;

/**
 * 用户对象转换器。
 */
@Component
public class UserConverter {

    public UserProfileResponse toUserProfile(Long userId, String username) {
        return UserProfileResponse.builder().userId(userId).username(username).build();
    }
}
