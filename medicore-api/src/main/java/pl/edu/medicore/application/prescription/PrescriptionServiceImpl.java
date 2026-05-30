package pl.edu.medicore.application.prescription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.application.record.RecordService;
import pl.edu.medicore.common.encryption.HashId;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final RecordService recordService;

    @Override
    @Transactional
    public HashId create(PrescriptionCreateDto dto) {
        if (dto.endDate() != null && dto.startDate().isAfter(dto.endDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        Prescription prescription = prescriptionMapper.toEntity(dto, recordService.getById(dto.recordId()));
        Prescription saved = prescriptionRepository.save(prescription);
        return HashId.of(saved.getId());
    }
}
