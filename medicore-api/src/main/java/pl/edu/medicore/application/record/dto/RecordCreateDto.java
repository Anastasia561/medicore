package pl.edu.medicore.application.record.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.edu.medicore.common.encryption.HashId;

public record RecordCreateDto(
        @NotNull(message = "Appointment ID is required")
        HashId appointmentId,

        @NotNull(message = "Diagnosis is required")
        @Size(min = 3, max = 100, message = "Diagnosis must be between 3 and 100 characters")
        String diagnosis,

        @NotNull(message = "Summary is required")
        @Size(min = 10, max = 255, message = "Summary must be between 10 and 255 characters")
        String summary
) {
}
