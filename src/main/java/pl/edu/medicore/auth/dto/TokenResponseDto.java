package pl.edu.medicore.auth.dto;

public record TokenResponseDto (String accessToken, String refreshToken) {
}
