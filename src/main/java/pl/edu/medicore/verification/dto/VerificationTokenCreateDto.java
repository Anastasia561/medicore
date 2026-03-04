package pl.edu.medicore.verification.dto;

import pl.edu.medicore.verification.model.TokenType;

import java.time.Duration;

public record VerificationTokenCreateDto(
        TokenType tokenType,
        String tokenHash,
        String email,
        Duration validDuration
) {
}
