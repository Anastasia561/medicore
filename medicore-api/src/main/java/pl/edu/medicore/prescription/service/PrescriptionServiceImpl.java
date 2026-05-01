package pl.edu.medicore.prescription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.prescription.mapper.PrescriptionMapper;
import pl.edu.medicore.prescription.model.Prescription;
import pl.edu.medicore.prescription.repository.PrescriptionRepository;
import pl.edu.medicore.record.service.RecordService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final RecordService recordService;

    @Override
    @Transactional
    public UUID create(PrescriptionCreateDto dto) {
        if (dto.endDate() != null && dto.startDate().isAfter(dto.endDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        Prescription prescription = prescriptionMapper.toEntity(dto, recordService.getByPublicId(dto.recordId()));
        return prescriptionRepository.save(prescription).getPublicId();
    }
}
