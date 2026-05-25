package pl.edu.medicore.application.prescription;

import pl.edu.medicore.application.prescription.dto.PrescriptionCreateDto;

import java.util.UUID;

public interface PrescriptionService {
    UUID create(PrescriptionCreateDto dto);
}
