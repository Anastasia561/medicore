package pl.edu.medicore.application.record.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.doctor.dto.DoctorForRecordDto;

@Getter
@Setter
public class RecordForPatientPreviewDto extends RecordPreviewDto {
    private DoctorForRecordDto doctor;
}
