package pl.edu.medicore.application.doctor.dto;

import pl.edu.medicore.application.doctor.Specialization;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;

public record DoctorResponseDto(
        HashId id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Specialization specialization,
        Integer experience,
        LocalDate employmentDate
) {
}
