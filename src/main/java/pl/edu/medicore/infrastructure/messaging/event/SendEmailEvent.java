package pl.edu.medicore.infrastructure.messaging.event;

import pl.edu.medicore.email.model.EmailType;

public record SendEmailEvent<T>(
        String to,
        EmailType emailType,
        T dto
) {
}
