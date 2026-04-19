package pl.edu.medicore.appointment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentForDoctorDto extends AppointmentInfoDto {
    private String phoneNumber;
}

