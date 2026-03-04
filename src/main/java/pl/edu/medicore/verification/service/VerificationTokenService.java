package pl.edu.medicore.verification.service;

import pl.edu.medicore.verification.model.TokenType;

public interface VerificationTokenService {
    String createToken(String email, TokenType tokenType);

    void validateToken(String rawToken, TokenType type, String email);
}
