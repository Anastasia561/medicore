package pl.edu.medicore.record.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.doctor.dto.DoctorForRecordDto;

@Getter
@Setter
public class RecordForPatientPreviewDto extends RecordPreviewDto {
    private DoctorForRecordDto doctor;
}
