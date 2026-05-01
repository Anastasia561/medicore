package pl.edu.medicore.prescription.service;

import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;

import java.util.UUID;

public interface PrescriptionService {
    UUID create(PrescriptionCreateDto dto);
}
