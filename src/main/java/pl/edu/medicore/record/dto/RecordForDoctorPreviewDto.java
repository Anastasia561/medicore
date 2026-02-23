package pl.edu.medicore.record.dto;

import pl.edu.medicore.patient.dto.PatientForRecordDto;

import java.time.LocalDate;

public record RecordForDoctorPreviewDto(
        PatientForRecordDto patient,
        LocalDate date
) {
}
