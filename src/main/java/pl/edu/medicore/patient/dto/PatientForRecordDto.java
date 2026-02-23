package pl.edu.medicore.patient.dto;

public record PatientForRecordDto(
        String firstName,
        String lastName,
        String email
) {
}
