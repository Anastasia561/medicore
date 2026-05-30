package pl.edu.medicore.infrastructure.messaging.event;

import pl.edu.medicore.common.encryption.HashId;

public record PatientUpdateEvent(HashId patientId) {
}
