package pl.edu.medicore.verification.service;

import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;

import java.time.Duration;

public interface VerificationTokenService {
    String createToken(String email, TokenType tokenType, Duration duration);

    void validateToken(String rawToken, TokenType type, String email);

    VerificationToken findLatestByEmailAndTokenType(String email, TokenType type);
}
