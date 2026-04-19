package pl.edu.medicore.profile.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.doctor.model.Specialization;

import java.time.LocalDate;

@Getter
@Setter
public class DoctorProfileResponseDto extends ProfileResponseDto {
    private int experience;
    private LocalDate employmentDate;
    private Specialization specialization;
}
