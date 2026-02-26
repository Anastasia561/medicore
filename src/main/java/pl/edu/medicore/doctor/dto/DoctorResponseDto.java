package pl.edu.medicore.doctor.dto;

import pl.edu.medicore.doctor.model.Specialization;

import java.time.LocalDate;

public record DoctorResponseDto(
        String firstName,
        String lastName,
        String email,
        Specialization specialization,
        Integer experience,
        LocalDate employmentDate
) {
}
