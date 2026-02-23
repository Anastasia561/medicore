package pl.edu.medicore.doctor.dto;

import pl.edu.medicore.doctor.model.Specialization;

public record DoctorForRecordDto(String firstName, String lastName, Specialization specialization) {
}
