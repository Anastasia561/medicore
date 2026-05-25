package pl.edu.medicore.application.verification;

import java.time.Duration;
import java.time.Instant;

public interface VerificationTokenService {
    String createToken(String email, TokenType tokenType, Duration duration);

    void validateToken(String rawToken, TokenType type, String email);

    VerificationToken findLatestByEmailAndTokenType(String email, TokenType type);

    void deleteAllExpiredBefore(Instant now);
}
