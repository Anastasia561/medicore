package pl.edu.medicore.prescription.service;

import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;

public interface PrescriptionService {
    long create(PrescriptionCreateDto dto);
}
