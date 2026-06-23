package pl.edu.medicore.application.record.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.patient.dto.PatientForRecordPreviewDto;

@Getter
@Setter
public class RecordForDoctorPreviewDto extends RecordPreviewDto {
    private PatientForRecordPreviewDto patient;
}

