package pl.edu.medicore.application.appointment.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.appointment.AppointmentStatus;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public abstract class AppointmentInfoDto {
    private HashId id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String firstName;
    private String lastName;
}
