package pl.edu.medicore.application.email.dto;

public record VerificationEmailDto(
        String firstName,
        String lastName,
        String url
) {
}
