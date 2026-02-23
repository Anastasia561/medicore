package pl.edu.medicore.prescription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.prescription.mapper.PrescriptionMapper;
import pl.edu.medicore.prescription.model.Prescription;
import pl.edu.medicore.prescription.repository.PrescriptionRepository;
import pl.edu.medicore.record.service.RecordService;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final RecordService recordService;

    @Override
    @Transactional
    public long create(PrescriptionCreateDto dto) {
        if (dto.startDate().isAfter(dto.endDate())) {
            throw new IllegalArgumentException("Start date must be after end date");
        }
        Prescription prescription = prescriptionMapper.toEntity(dto, recordService.getById(dto.recordId()));
        return prescriptionRepository.save(prescription).getId();
    }
}
