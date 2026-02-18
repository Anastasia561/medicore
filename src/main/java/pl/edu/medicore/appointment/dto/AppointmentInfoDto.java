package pl.edu.medicore.appointment.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.appointment.model.Status;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public abstract class AppointmentInfoDto {
    private LocalDate date;
    private LocalTime time;
    private Status status;
    private String firstName;
    private String lastName;
}
