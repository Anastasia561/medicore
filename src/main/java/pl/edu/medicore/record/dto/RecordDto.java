package pl.edu.medicore.record.dto;

import pl.edu.medicore.doctor.dto.DoctorForRecordDto;
import pl.edu.medicore.prescription.dto.PrescriptionDto;

import java.time.LocalDate;
import java.util.List;

public record RecordDto(
        DoctorForRecordDto doctor,
        LocalDate date,
        String diagnosis,
        String summary,
        List<PrescriptionDto> prescriptions
) {
}
