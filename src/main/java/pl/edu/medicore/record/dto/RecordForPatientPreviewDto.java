package pl.edu.medicore.record.dto;

import pl.edu.medicore.doctor.dto.DoctorForRecordDto;

import java.time.LocalDate;

public record RecordForPatientPreviewDto(
        DoctorForRecordDto doctor,
        LocalDate date
) {
}
