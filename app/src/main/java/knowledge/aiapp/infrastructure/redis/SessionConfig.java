package knowledge.aiapp.infrastructure.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Session Cookie 配置。
 */
@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer(@Value("${server.servlet.session.cookie.name:KNOWLEDGE_SESSION}")
                                             String cookieName,
                                             @Value("${server.servlet.session.cookie.http-only:true}")
                                             boolean useHttpOnlyCookie,
                                             @Value("${server.servlet.session.cookie.secure:false}")
                                             boolean useSecureCookie,
                                             @Value("${server.servlet.session.cookie.same-site:Lax}")
                                             String sameSite,
                                             @Value("${server.servlet.session.cookie.path:/}")
                                             String cookiePath) {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(cookieName);
        serializer.setUseHttpOnlyCookie(useHttpOnlyCookie);
        serializer.setUseSecureCookie(useSecureCookie);
        serializer.setSameSite(sameSite);
        serializer.setCookiePath(cookiePath);
        return serializer;
    }
}
