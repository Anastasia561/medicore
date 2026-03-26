package pl.edu.medicore.auth.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.auth.dto.AuthRequestDto;
import pl.edu.medicore.auth.dto.PasswordResetDto;
import pl.edu.medicore.auth.dto.TokenResponseDto;
import pl.edu.medicore.auth.jwt.service.JwtService;
import pl.edu.medicore.auth.refreshtoken.service.RefreshTokenService;
import pl.edu.medicore.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.email.dto.VerificationEmailDto;
import pl.edu.medicore.email.model.EmailType;
import pl.edu.medicore.email.service.EmailService;
import pl.edu.medicore.exception.InvalidRefreshTokenException;
import pl.edu.medicore.person.mapper.PersonMapper;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.service.PersonService;
import pl.edu.medicore.utils.UrlBuilder;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;
import pl.edu.medicore.verification.service.VerificationTokenService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private PersonService personService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private VerificationTokenService verificationTokenService;
    @Mock
    private PersonMapper personMapper;
    @Mock
    private EmailService emailService;
    @Mock
    private UrlBuilder urlBuilder;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldReturnTokens_whenLoginWithValidCredentials() {
        AuthRequestDto request = new AuthRequestDto("test@mail.com", "password");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@mail.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        Person person = new Person();
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.generateAccessToken(userDetails)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(userDetails)).thenReturn(refreshToken);
        when(personService.getByEmail("test@mail.com")).thenReturn(person);

        TokenResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());

        verify(refreshTokenService).create(person, refreshToken);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowBadCredentialsExceptionException_whenLoginWithInvalidCredentials() {
        AuthRequestDto request = new AuthRequestDto("wrong@mail.com", "wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(request));

        assertEquals("Invalid email or password", exception.getMessage());

        verify(jwtService, never()).generateAccessToken(any());
        verify(refreshTokenService, never()).create(any(), any());
    }

    @Test
    void shouldReturnNewAccessToken_whenTokenIsValid() {
        String refreshToken = "valid-refresh-token";
        String username = "test@mail.com";
        String newAccessToken = "new-access-token";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(refreshToken, userDetails)).thenReturn(true);
        when(jwtService.generateAccessToken(userDetails)).thenReturn(newAccessToken);

        TokenResponseDto response = authService.refresh(refreshToken);

        assertNotNull(response);
        assertEquals(newAccessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());

        verify(jwtService).isRefreshToken(refreshToken);
        verify(jwtService).extractUsername(refreshToken);
        verify(jwtService).isTokenValid(refreshToken, userDetails);
        verify(jwtService).generateAccessToken(userDetails);
    }

    @Test
    void shouldThrowInvalidRefreshTokenException_whenTokenNotRefreshToken() {
        String token = "not-refresh-token";

        when(jwtService.isRefreshToken(token)).thenReturn(false);

        InvalidRefreshTokenException exception = assertThrows(InvalidRefreshTokenException.class,
                () -> authService.refresh(token));

        assertEquals("Token is not a refresh token", exception.getMessage());

        verify(jwtService).isRefreshToken(token);
        verify(jwtService, never()).extractUsername(any());
    }

    @Test
    void shouldThrowInvalidRefreshTokenException_whenTokenInvalid() {
        String refreshToken = "invalid-refresh-token";
        String username = "test@mail.com";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(refreshToken, userDetails)).thenReturn(false);

        InvalidRefreshTokenException exception = assertThrows(InvalidRefreshTokenException.class,
                () -> authService.refresh(refreshToken));

        assertEquals("Refresh token is invalid", exception.getMessage());

        verify(jwtService).isTokenValid(refreshToken, userDetails);
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void shouldThrowInvalidRefreshTokenException_whenJwtExceptionOccurs() {
        String refreshToken = "broken-token";

        when(jwtService.isRefreshToken(refreshToken)).thenThrow(new JwtException("JWT error"));

        InvalidRefreshTokenException exception = assertThrows(InvalidRefreshTokenException.class,
                () -> authService.refresh(refreshToken));

        assertEquals("Invalid refresh token", exception.getMessage());
    }

    @Test
    void shouldRevokeToken_whenTokenIsValid() {
        String refreshToken = "valid-refresh-token";
        String username = "test@mail.com";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.isTokenValid(refreshToken, userDetails)).thenReturn(true);

        authService.logout(refreshToken);

        verify(jwtService).extractUsername(refreshToken);
        verify(jwtService).isRefreshToken(refreshToken);
        verify(jwtService).isTokenValid(refreshToken, userDetails);
        verify(refreshTokenService).revoke(refreshToken);
    }

    @Test
    void shouldThrowInvalidRefreshTokenException_whenTokenNotRefreshTokenForLogout() {
        String token = "not-refresh-token";
        String username = "test@mail.com";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isRefreshToken(token)).thenReturn(false);

        InvalidRefreshTokenException exception = assertThrows(InvalidRefreshTokenException.class,
                () -> authService.logout(token));

        assertEquals("Invalid refresh token", exception.getMessage());

        verify(jwtService).isRefreshToken(token);
        verify(refreshTokenService, never()).revoke(any());
    }

    @Test
    void shouldThrowInvalidRefreshTokenException_whenTokenInvalidForLogout() {
        String refreshToken = "invalid-refresh-token";
        String username = "test@mail.com";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(jwtService.extractUsername(refreshToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.isTokenValid(refreshToken, userDetails)).thenReturn(false);

        InvalidRefreshTokenException exception = assertThrows(InvalidRefreshTokenException.class,
                () -> authService.logout(refreshToken));

        assertEquals("Invalid refresh token", exception.getMessage());

        verify(jwtService).isTokenValid(refreshToken, userDetails);
        verify(refreshTokenService, never()).revoke(any());
    }

    @Test
    void shouldCreateResetToken_whenRequestIsValid() {
        String email = "test@mail.com";

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmail(email);

        String token = "reset-token";
        String link = "http://reset-link";

        when(verificationTokenService.findLatestByEmailAndTokenType(email, TokenType.PASSWORD_RESET)).thenReturn(null);

        when(personService.getByEmail(email)).thenReturn(person);
        when(verificationTokenService.createToken(eq(email), eq(TokenType.PASSWORD_RESET), any())).thenReturn(token);
        when(urlBuilder.buildPasswordResetUrl(token)).thenReturn(link);

        authService.createResetToken(email);

        verify(verificationTokenService).createToken(eq(email), eq(TokenType.PASSWORD_RESET), any());
        verify(urlBuilder).buildPasswordResetUrl(token);
        verify(emailService).sendEmail(eq(email), eq(EmailType.PASSWORD_RESET_REQUEST), any(VerificationEmailDto.class));
    }

    @Test
    void shouldThrowIllegalStateException_whenRequestTooFrequent() {
        String email = "test@mail.com";

        VerificationToken recentToken = new VerificationToken();
        recentToken.setCreatedAt(Instant.now());

        when(verificationTokenService.findLatestByEmailAndTokenType(email, TokenType.PASSWORD_RESET))
                .thenReturn(recentToken);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> authService.createResetToken(email));

        assertEquals("Reset password request too frequent", exception.getMessage());

        verify(verificationTokenService, never()).createToken(any(), any(), any());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    void shouldCreateResetToken_whenLastTokenIsOld() {
        String email = "test@mail.com";

        VerificationToken oldToken = new VerificationToken();
        oldToken.setCreatedAt(Instant.now().minusSeconds(120));

        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmail(email);

        String token = "reset-token";
        String link = "http://reset-link";

        when(verificationTokenService.findLatestByEmailAndTokenType(email, TokenType.PASSWORD_RESET))
                .thenReturn(oldToken);

        when(personService.getByEmail(email)).thenReturn(person);
        when(verificationTokenService.createToken(eq(email), eq(TokenType.PASSWORD_RESET), any())).thenReturn(token);
        when(urlBuilder.buildPasswordResetUrl(token)).thenReturn(link);

        authService.createResetToken(email);

        verify(emailService).sendEmail(eq(email), eq(EmailType.PASSWORD_RESET_REQUEST), any());
    }

    @Test
    void shouldUpdatePasswordAndSendEmail_whenTokenIsValid() {
        PasswordResetDto dto = new PasswordResetDto("valid-token", "test@mail.com",
                "newPassword", "newPassword");

        Person person = new Person();
        ConfirmationEmailDto emailDto = new ConfirmationEmailDto("John", "Doe");

        when(personService.getByEmail(dto.email())).thenReturn(person);
        when(personMapper.toEmailDto(person)).thenReturn(emailDto);

        authService.resetPassword(dto);

        verify(verificationTokenService).validateToken(dto.token(), TokenType.PASSWORD_RESET, dto.email());
        verify(personService).updatePassword(dto);
        verify(personService).getByEmail(dto.email());
        verify(personMapper).toEmailDto(person);

        verify(emailService).sendEmail(dto.email(), EmailType.PASSWORD_RESET_CONFIRM, emailDto);
    }

    @Test
    void shouldNotUpdatePassword_whenTokenIsInvalid() {
        PasswordResetDto dto = new PasswordResetDto("valid-token", "test@mail.com",
                "newPassword", "newPassword");

        doThrow(new IllegalArgumentException("Invalid token"))
                .when(verificationTokenService)
                .validateToken(dto.token(), TokenType.PASSWORD_RESET, dto.email());

        assertThrows(IllegalArgumentException.class,
                () -> authService.resetPassword(dto));

        verify(personService, never()).updatePassword(any());
        verify(emailService, never()).sendEmail(any(), any(), any());
    }
}
