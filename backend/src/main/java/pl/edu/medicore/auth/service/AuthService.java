package pl.edu.medicore.auth.service;


import pl.edu.medicore.auth.dto.AuthRequestDto;
import pl.edu.medicore.auth.dto.PasswordResetDto;
import pl.edu.medicore.auth.dto.TokenResponseDto;

public interface AuthService {
    TokenResponseDto login(AuthRequestDto request);

    TokenResponseDto refresh(String refreshToken);

    void logout(String refreshToken);

    void createResetToken(String email);

    void resetPassword(PasswordResetDto dto);
}
