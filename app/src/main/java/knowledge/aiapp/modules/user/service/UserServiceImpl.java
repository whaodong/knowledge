package knowledge.aiapp.modules.user.service;

import knowledge.aiapp.modules.user.converter.UserConverter;
import knowledge.aiapp.modules.user.dto.UserProfileResponse;
import org.springframework.stereotype.Service;

/**
 * 用户服务默认实现。
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserConverter userConverter;

    public UserServiceImpl(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return userConverter.toUserProfile(userId, "demo-user");
    }
}
