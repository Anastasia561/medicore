package pl.edu.medicore.application.record.dto;

import pl.edu.medicore.application.doctor.dto.DoctorForRecordDto;
import pl.edu.medicore.application.patient.dto.PatientForRecordFullDto;
import pl.edu.medicore.application.patient.dto.PatientForRecordPreviewDto;
import pl.edu.medicore.application.prescription.dto.PrescriptionDto;

import java.time.LocalDate;
import java.util.List;

public record RecordDto(
        DoctorForRecordDto doctor,
        PatientForRecordFullDto patient,
        LocalDate date,
        String diagnosis,
        String summary,
        List<PrescriptionDto> prescriptions
) {
}
