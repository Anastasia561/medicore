package pl.edu.medicore.application.verification;

import java.time.Duration;

public record VerificationTokenCreateDto(
        TokenType tokenType,
        String tokenHash,
        String email,
        Duration validDuration
) {
}
