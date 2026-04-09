package knowledge.aiapp.modules.user.repository;

import java.util.Optional;
import knowledge.aiapp.modules.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户仓储。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
