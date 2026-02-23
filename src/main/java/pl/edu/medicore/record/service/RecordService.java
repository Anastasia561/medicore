package pl.edu.medicore.record.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordForDoctorPreviewDto;
import pl.edu.medicore.record.dto.RecordForPatientPreviewDto;
import pl.edu.medicore.record.model.Record;

public interface RecordService {
    RecordDto getByAppointmentId(Long id);

    Page<RecordForPatientPreviewDto> getAllByPatientId(Long patientId, Pageable pageable);

    Page<RecordForDoctorPreviewDto> getAllByDoctorAndPatientId(Long doctorId, Long patientId, Pageable pageable);

    long create(RecordCreateDto dto);

    Record getById(Long id);
}
