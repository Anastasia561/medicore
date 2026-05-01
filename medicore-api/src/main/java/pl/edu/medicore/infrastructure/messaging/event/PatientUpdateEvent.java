package pl.edu.medicore.infrastructure.messaging.event;

import java.util.UUID;

public record PatientUpdateEvent(UUID patientId) {
}
