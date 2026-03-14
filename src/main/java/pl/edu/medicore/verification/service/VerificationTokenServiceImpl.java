package pl.edu.medicore.verification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.verification.dto.VerificationTokenCreateDto;
import pl.edu.medicore.verification.mapper.VerificationTokenMapper;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;
import pl.edu.medicore.verification.repository.VerificationTokenRepository;

import java.time.Duration;
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
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = passwordEncoder.encode(rawToken);
        VerificationTokenCreateDto dto = new VerificationTokenCreateDto(tokenType, tokenHash, email, duration);
        tokenRepository.save(tokenMapper.toEntity(dto));
        return rawToken;
    }

    @Override
    public void validateToken(String rawToken, TokenType type, String email) {
        List<VerificationToken> tokens = tokenRepository.findActiveTokensByEmailAndType(email, type);

        VerificationToken token = tokens.stream()
                .filter(t -> passwordEncoder.matches(rawToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
        tokenRepository.delete(token);
    }
}
