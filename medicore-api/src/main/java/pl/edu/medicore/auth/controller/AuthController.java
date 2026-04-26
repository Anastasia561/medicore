package pl.edu.medicore.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.auth.dto.AuthRequestDto;
import pl.edu.medicore.auth.dto.AuthResponseDto;
import pl.edu.medicore.auth.dto.PasswordResetDto;
import pl.edu.medicore.auth.dto.PasswordResetRequestDto;
import pl.edu.medicore.auth.dto.TokenResponseDto;
import pl.edu.medicore.auth.jwt.JwtProperties;
import pl.edu.medicore.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Managing login and logout, password reset operations")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProperties jwtProperties;

    @Operation(summary = "Login endpoint")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        TokenResponseDto dto = authService.login(authRequestDto);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", dto.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(jwtProperties.getRefreshTokenExpirationTimeSec())
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponseDto(dto.accessToken()));
    }

    @Operation(summary = "Endpoint for token refresh")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        TokenResponseDto dto = authService.refresh(refreshToken);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", dto.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(jwtProperties.getRefreshTokenExpirationTimeSec())
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponseDto(dto.accessToken()));
    }

    @Operation(summary = "Logout endpoint")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        authService.logout(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/api/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Operation(summary = "Request password reset endpoint")
    @PostMapping("/reset-password/request")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void requestPasswordReset(@Valid @RequestBody PasswordResetRequestDto dto) {
        authService.createResetToken(dto.email());
    }

    @Operation(summary = "Reset password endpoint")
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody PasswordResetDto dto) {
        authService.resetPassword(dto);
    }
}
