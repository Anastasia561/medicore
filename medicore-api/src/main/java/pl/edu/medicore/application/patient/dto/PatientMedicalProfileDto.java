package pl.edu.medicore.application.patient.dto;

import pl.edu.medicore.application.patient.PregnancyStatus;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.common.encryption.HashId;

public record PatientMedicalProfileDto(
        HashId id,
        Double weight,
        Double height,
        PregnancyStatus pregnancyStatus,
        Gender gender
) {
}
