package pl.edu.medicore.email.dto;

import pl.edu.medicore.consultation.model.Workday;

public record ScheduleEmailDto(
        Workday day,
        String firstName,
        String lastName
) {
}
