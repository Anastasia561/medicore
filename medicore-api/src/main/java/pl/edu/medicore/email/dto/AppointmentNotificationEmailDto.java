package pl.edu.medicore.email.dto;

import pl.edu.medicore.doctor.model.Specialization;

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
