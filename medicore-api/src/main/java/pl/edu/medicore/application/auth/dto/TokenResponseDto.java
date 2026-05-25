package pl.edu.medicore.application.auth.dto;

public record TokenResponseDto (String accessToken, String refreshToken) {
}
