package pl.edu.medicore.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.jwt")
@Component
public class JwtProperties {
    private String secret;
    private long accessTokenExpirationTimeMs;
    private long refreshTokenExpirationTimeMs;

    public long getRefreshTokenExpirationTimeSec() {
        return refreshTokenExpirationTimeMs / 1000;
    }
}
