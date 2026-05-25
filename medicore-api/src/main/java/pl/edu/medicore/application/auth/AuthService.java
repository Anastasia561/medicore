package pl.edu.medicore.application.auth;


import pl.edu.medicore.application.auth.dto.AuthRequestDto;
import pl.edu.medicore.application.auth.dto.PasswordResetDto;
import pl.edu.medicore.application.auth.dto.TokenResponseDto;

public interface AuthService {
    TokenResponseDto login(AuthRequestDto request);

    TokenResponseDto refresh(String refreshToken);

    void logout(String refreshToken);

    void createResetToken(String email);

    void resetPassword(PasswordResetDto dto);
}
