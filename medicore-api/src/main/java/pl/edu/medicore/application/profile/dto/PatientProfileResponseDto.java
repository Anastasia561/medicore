package pl.edu.medicore.application.profile.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.patient.PregnancyStatus;

@Getter
@Setter
public class PatientProfileResponseDto extends ProfileResponseDto {
    private double weight;
    private double height;
    private PregnancyStatus pregnancyStatus;
}
