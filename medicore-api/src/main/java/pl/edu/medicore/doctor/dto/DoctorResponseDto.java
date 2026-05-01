package pl.edu.medicore.doctor.dto;

import pl.edu.medicore.doctor.model.Specialization;

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
