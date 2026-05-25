package pl.edu.medicore.application.email.dto;

import pl.edu.medicore.application.doctor.Specialization;

public record AppointmentNotificationEmailDto(
        String patientFirstName,
        String patientLastName,
        String doctorFirstName,
        String doctorLastName,
        Specialization specialization,
        String date,
        String time
) {
}
