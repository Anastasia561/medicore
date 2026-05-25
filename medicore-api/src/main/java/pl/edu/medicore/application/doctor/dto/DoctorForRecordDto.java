package pl.edu.medicore.application.doctor.dto;

import pl.edu.medicore.application.doctor.Specialization;

public record DoctorForRecordDto(String firstName, String lastName, Specialization specialization) {
}
