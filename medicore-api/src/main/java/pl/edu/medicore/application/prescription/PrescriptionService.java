package pl.edu.medicore.application.prescription;

import pl.edu.medicore.application.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.common.encryption.HashId;

import java.util.UUID;

public interface PrescriptionService {
    HashId create(PrescriptionCreateDto dto);
}
