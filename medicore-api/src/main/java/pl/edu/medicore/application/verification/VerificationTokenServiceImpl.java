package pl.edu.medicore.application.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.verification.dto.VerificationTokenCreateDto;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository tokenRepository;
    private final VerificationTokenMapper tokenMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String createToken(String email, TokenType tokenType, Duration duration) {
        String normalizedEmail = email.toLowerCase().trim();
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = passwordEncoder.encode(rawToken);

        VerificationTokenCreateDto dto = new VerificationTokenCreateDto(tokenType, tokenHash, normalizedEmail, duration);
        tokenRepository.save(tokenMapper.toEntity(dto));

        if (tokenType == TokenType.EMAIL_VERIFICATION || tokenType == TokenType.PASSWORD_RESET) {
            return encodeEmailWithToken(normalizedEmail, rawToken);
        }

        return rawToken;
    }

    @Override
    @Transactional
    public String validateTokenAndGetEmail(String compositeToken, TokenType type) {
        String email = decodeEmailFromToken(compositeToken, type);
        if (email == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        String rawToken = extractRawToken(compositeToken);
        validateToken(rawToken, type, email);
        return email;
    }

    @Override
    @Transactional
    public void validateToken(String rawToken, TokenType type, String email) {
        String normalizedEmail = email.toLowerCase().trim();
        List<VerificationToken> tokens = tokenRepository.findActiveTokensByEmailAndType(normalizedEmail, type, Instant.now());

        VerificationToken validToken = tokens.stream()
                .filter(t -> passwordEncoder.matches(rawToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        tokenRepository.delete(validToken);
    }

    @Override
    public VerificationToken findLatestByEmailAndTokenType(String email, TokenType type) {
        return tokenRepository
                .findLatestByEmailAndTokenType(email.toLowerCase().trim(), type) // Fixed hardcoded TokenType.PASSWORD_RESET
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteAllExpiredBefore(Instant now) {
        tokenRepository.deleteAllByExpiresAtBefore(now);
    }

    private String encodeEmailWithToken(String email, String rawToken) {
        String encodedEmail = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(email.getBytes(StandardCharsets.UTF_8));
        return encodedEmail + "." + rawToken;
    }

    private String decodeEmailFromToken(String token, TokenType type) {
        if ((type != TokenType.EMAIL_VERIFICATION && type != TokenType.PASSWORD_RESET) || token == null || token.isBlank()) {
            return null;
        }

        int separatorIndex = token.indexOf('.');
        if (separatorIndex <= 0 || separatorIndex == token.length() - 1) {
            return null;
        }

        String encodedEmail = token.substring(0, separatorIndex);
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedEmail);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String extractRawToken(String token) {
        if (token == null || token.isBlank()) {
            return token;
        }

        int separatorIndex = token.indexOf('.');
        if (separatorIndex >= 0 && separatorIndex < token.length() - 1) {
            return token.substring(separatorIndex + 1);
        }

        return token;
    }
}
