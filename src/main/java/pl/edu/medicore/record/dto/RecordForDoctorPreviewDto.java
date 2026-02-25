package pl.edu.medicore.record.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.patient.dto.PatientForRecordDto;

@Getter
@Setter
public class RecordForDoctorPreviewDto extends RecordPreviewDto {
    private PatientForRecordDto patient;
}

