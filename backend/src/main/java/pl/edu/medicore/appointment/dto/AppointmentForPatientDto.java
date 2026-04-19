package pl.edu.medicore.appointment.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.doctor.model.Specialization;

@Getter
@Setter
public class AppointmentForPatientDto extends AppointmentInfoDto {
    private Specialization specialization;
}
