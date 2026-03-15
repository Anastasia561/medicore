package pl.edu.medicore.email.dto;

public record VerificationEmailDto(
        String firstName,
        String lastName,
        String url
) {
}
