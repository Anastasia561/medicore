package pl.edu.medicore.application.verification.dto;

import pl.edu.medicore.application.verification.TokenType;

import java.time.Duration;

public record VerificationTokenCreateDto(
        TokenType tokenType,
        String tokenHash,
        String email,
        Duration validDuration
) {
}
