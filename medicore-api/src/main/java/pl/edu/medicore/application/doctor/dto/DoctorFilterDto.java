package pl.edu.medicore.application.doctor.dto;

import pl.edu.medicore.application.doctor.Specialization;

public record DoctorFilterDto(
        String query,
        Specialization specialization
) {
}
