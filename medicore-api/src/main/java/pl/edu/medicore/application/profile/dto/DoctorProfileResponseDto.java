package pl.edu.medicore.application.profile.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.doctor.Specialization;

import java.time.LocalDate;

@Getter
@Setter
public class DoctorProfileResponseDto extends ProfileResponseDto {
    private LocalDate employmentDate;
    private Specialization specialization;
}
