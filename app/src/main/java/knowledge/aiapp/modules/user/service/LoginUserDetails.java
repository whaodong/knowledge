package knowledge.aiapp.modules.user.service;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录用户详情。
 */
@Getter
public class LoginUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String nickname;
    private final boolean enabled;
    private final List<? extends GrantedAuthority> authorities;

    public LoginUserDetails(Long userId,
                            String username,
                            String password,
                            String nickname,
                            boolean enabled,
                            List<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
