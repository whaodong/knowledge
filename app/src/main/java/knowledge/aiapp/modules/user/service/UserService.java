package knowledge.aiapp.modules.user.service;

import knowledge.aiapp.modules.user.dto.UserProfileResponse;

/**
 * 用户服务。
 */
public interface UserService {

    UserProfileResponse getProfile(Long userId);
}
