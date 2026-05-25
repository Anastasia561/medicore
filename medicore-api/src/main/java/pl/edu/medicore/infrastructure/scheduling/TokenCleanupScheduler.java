package pl.edu.medicore.infrastructure.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.edu.medicore.application.auth.refreshtoken.RefreshTokenService;
import pl.edu.medicore.application.verification.VerificationTokenService;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;

    @Scheduled(cron = "0 0 3 * * *", zone = "Europe/Warsaw")
    public void removeExpiredTokens() {
        refreshTokenService.deleteAllExpiredBefore(Instant.now());
        verificationTokenService.deleteAllExpiredBefore(Instant.now());
    }
}
