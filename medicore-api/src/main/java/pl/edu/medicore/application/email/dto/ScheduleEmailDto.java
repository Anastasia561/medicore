package pl.edu.medicore.application.email.dto;

import pl.edu.medicore.application.consultation.Workday;

public record ScheduleEmailDto(
        Workday day,
        String firstName,
        String lastName
) {
}
