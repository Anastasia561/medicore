package pl.edu.medicore.doctor.dto;

import pl.edu.medicore.doctor.model.Specialization;

public record DoctorFilterDto(
        String query,
        Specialization specialization
) {
}
