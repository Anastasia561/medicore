package pl.edu.medicore.application.patient.dto;

import pl.edu.medicore.application.patient.PregnancyStatus;
import pl.edu.medicore.application.person.Gender;

public record PatientForRecordFullDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Double weight,
        Double height,
        PregnancyStatus pregnancyStatus,
        Gender gender
) {
}
