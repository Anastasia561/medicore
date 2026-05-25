package pl.edu.medicore.application.record.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.patient.dto.PatientForRecordDto;

@Getter
@Setter
public class RecordForDoctorPreviewDto extends RecordPreviewDto {
    private PatientForRecordDto patient;
}

