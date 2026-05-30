package pl.edu.medicore.application.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.record.dto.RecordCreateDto;
import pl.edu.medicore.application.record.dto.RecordDto;
import pl.edu.medicore.application.record.dto.RecordFilterDto;
import pl.edu.medicore.application.record.dto.RecordPreviewDto;
import pl.edu.medicore.common.encryption.HashId;

public interface RecordService {
    RecordDto getByAppointmentId(HashId id);

    Page<RecordPreviewDto> getAllByPersonId(HashId id, Role role, RecordFilterDto filter, Pageable pageable);

    HashId create(RecordCreateDto dto);

    Record getById(HashId id);
}

