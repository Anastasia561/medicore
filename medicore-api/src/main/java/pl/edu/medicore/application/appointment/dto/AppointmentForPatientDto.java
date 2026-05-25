package pl.edu.medicore.application.appointment.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.doctor.Specialization;

@Getter
@Setter
public class AppointmentForPatientDto extends AppointmentInfoDto {
    private Specialization specialization;
}
