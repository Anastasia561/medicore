package pl.edu.medicore.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.medicore.common.config.properties.FrontendProperties;

@Component
@RequiredArgsConstructor
public class UrlBuilder {
    private final FrontendProperties frontendProperties;

    public String buildPasswordResetUrl(String token) {
        return frontendProperties.getFrontBaseUrl() + "/reset-password?token=" + token;
    }

    public String buildEmailVerificationUrl(String token) {
        return frontendProperties.getFrontBaseUrl() + "/verify-email?token=" + token;
    }

    public String buildDoctorRegistrationUrl(String token) {
        return frontendProperties.getFrontBaseUrl() + "/register?token=" + token;
    }
}
