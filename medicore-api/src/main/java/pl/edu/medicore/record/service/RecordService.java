package pl.edu.medicore.record.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordFilterDto;
import pl.edu.medicore.record.dto.RecordPreviewDto;
import pl.edu.medicore.record.model.Record;

import java.util.UUID;

public interface RecordService {
    RecordDto getByAppointmentId(UUID id);

    Page<RecordPreviewDto> getAllById(long id, Role role, RecordFilterDto filter, Pageable pageable);

    UUID create(RecordCreateDto dto);

    Record getByPublicId(UUID id);
}

