package pl.edu.medicore.application.doctor.dto;

import pl.edu.medicore.application.doctor.Specialization;

import java.time.LocalDate;
import java.util.UUID;

public record DoctorResponseDto(
        UUID publicId,
        String firstName,
        String lastName,
        String email,
        Specialization specialization,
        Integer experience,
        LocalDate employmentDate
) {
}
