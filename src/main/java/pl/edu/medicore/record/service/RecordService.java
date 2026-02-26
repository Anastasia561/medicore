package pl.edu.medicore.record.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordFilterDto;
import pl.edu.medicore.record.dto.RecordPreviewDto;
import pl.edu.medicore.record.model.Record;

public interface RecordService {
    RecordDto getByAppointmentId(Long id);

    Page<RecordPreviewDto> getAllById(CustomUserDetails userDetails, RecordFilterDto filter, Pageable pageable);

    long create(RecordCreateDto dto);

    Record getById(Long id);
}
