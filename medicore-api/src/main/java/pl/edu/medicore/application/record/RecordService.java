package pl.edu.medicore.application.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.record.dto.RecordCreateDto;
import pl.edu.medicore.application.record.dto.RecordDto;
import pl.edu.medicore.application.record.dto.RecordFilterDto;
import pl.edu.medicore.application.record.dto.RecordPreviewDto;

import java.util.UUID;

public interface RecordService {
    RecordDto getByAppointmentId(UUID id);

    Page<RecordPreviewDto> getAllById(long id, Role role, RecordFilterDto filter, Pageable pageable);

    UUID create(RecordCreateDto dto);

    Record getByPublicId(UUID id);
}

