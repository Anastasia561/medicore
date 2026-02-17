package pl.edu.medicore.address.dto;

public record PatientAddressDto(
        String country,
        String city,
        String street,
        Integer number
) {
}
