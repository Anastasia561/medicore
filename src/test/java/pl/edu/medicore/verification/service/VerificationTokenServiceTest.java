package pl.edu.medicore.verification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.medicore.verification.dto.VerificationTokenCreateDto;
import pl.edu.medicore.verification.mapper.VerificationTokenMapper;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;
import pl.edu.medicore.verification.repository.VerificationTokenRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {
    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private VerificationTokenMapper tokenMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private VerificationTokenServiceImpl tokenService;

    @Test
    void shouldCreateToken_whenInputIsValid() {
        String email = "test@mail.com";
        TokenType tokenType = TokenType.EMAIL_VERIFICATION;

        String encodedToken = "encoded-token";

        VerificationToken entity = new VerificationToken();

        when(passwordEncoder.encode(anyString())).thenReturn(encodedToken);
        when(tokenMapper.toEntity(any())).thenReturn(entity);
        when(tokenRepository.save(entity)).thenReturn(entity);

        String rawToken = tokenService.createToken(email, tokenType, Duration.ofMinutes(5));

        assertNotNull(rawToken);
        assertFalse(rawToken.isBlank());

        verify(passwordEncoder).encode(rawToken);
        verify(tokenMapper).toEntity(any(VerificationTokenCreateDto.class));
        verify(tokenRepository).save(entity);
    }

    @Test
    void shouldValidateAndDeleteToken_whenInputIsValid() {
        String email = "test@mail.com";
        String rawToken = "raw-token";
        String tokenHash = "encoded-token";
        TokenType type = TokenType.EMAIL_VERIFICATION;

        VerificationToken token = new VerificationToken();
        token.setTokenHash(tokenHash);

        when(tokenRepository.findActiveTokensByEmailAndType(email, type, Instant.now())).thenReturn(List.of(token));
        when(passwordEncoder.matches(rawToken, tokenHash)).thenReturn(true);

        tokenService.validateToken(rawToken, type, email);

        verify(tokenRepository).delete(token);
        verify(tokenRepository).findActiveTokensByEmailAndType(email, type, Instant.now());
        verify(passwordEncoder).matches(rawToken, tokenHash);
    }

    @Test
    void shouldThrowException_whenTokenInvalid() {
        String email = "test@mail.com";
        String rawToken = "raw-token";
        String tokenHash = "encoded-token";
        TokenType type = TokenType.EMAIL_VERIFICATION;

        VerificationToken token = new VerificationToken();
        token.setTokenHash(tokenHash);

        when(tokenRepository.findActiveTokensByEmailAndType(email, type, Instant.now())).thenReturn(List.of(token));
        when(passwordEncoder.matches(rawToken, tokenHash)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tokenService.validateToken(rawToken, type, email)
        );

        assertEquals("Invalid or expired token", ex.getMessage());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    void shouldReturnLatestToken_whenExists() {
        String email = "test@example.com";
        TokenType type = TokenType.PASSWORD_RESET;

        VerificationToken token = new VerificationToken();
        List<VerificationToken> tokens = List.of(token);

        Mockito.when(tokenRepository.findLatestByEmailAndTokenType(email, TokenType.PASSWORD_RESET)).thenReturn(tokens);

        VerificationToken result = tokenService.findLatestByEmailAndTokenType(email, type);

        assertNotNull(result);
        assertEquals(token, result);
        verify(tokenRepository).findLatestByEmailAndTokenType(email, TokenType.PASSWORD_RESET);
    }

    @Test
    void shouldReturnNull_whenNoTokenExists() {
        String email = "test@example.com";
        TokenType type = TokenType.PASSWORD_RESET;

        Mockito.when(tokenRepository.findLatestByEmailAndTokenType(email, TokenType.PASSWORD_RESET))
                .thenReturn(Collections.emptyList());

        VerificationToken result = tokenService.findLatestByEmailAndTokenType(email, type);

        assertNull(result);
        verify(tokenRepository).findLatestByEmailAndTokenType(email, TokenType.PASSWORD_RESET);
    }
}
