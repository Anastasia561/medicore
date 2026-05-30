package pl.edu.medicore.application.record;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.appointment.Appointment;
import pl.edu.medicore.application.appointment.Status;
import pl.edu.medicore.application.appointment.AppointmentService;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.record.dto.RecordCreateDto;
import pl.edu.medicore.application.record.dto.RecordDto;
import pl.edu.medicore.application.record.dto.RecordFilterDto;
import pl.edu.medicore.application.record.dto.RecordPreviewDto;
import pl.edu.medicore.common.encryption.HashId;

@Service
@RequiredArgsConstructor
class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;
    private final AppointmentService appointmentService;

    @Override
    public RecordDto getByAppointmentId(HashId id) {
        return recordRepository.findByAppointmentId(id.value())
                .map(recordMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    }

    @Override
    public Page<RecordPreviewDto> getAllByPersonId(HashId id, Role role, RecordFilterDto filter, Pageable pageable) {
        if (filter.startDate() != null && filter.endDate() != null
                && filter.startDate().isAfter(filter.endDate()))
            throw new IllegalArgumentException("Start date should be before end date");

        Page<Record> all = recordRepository.findAll(RecordSpecification
                .withFilter(id.value(), role, filter), pageable);

        return role == Role.DOCTOR ? all.map(recordMapper::toDoctorPreviewDto)
                : all.map(recordMapper::toPatientPreviewDto);
    }

    @Override
    @Transactional
    public HashId create(RecordCreateDto dto) {
        Appointment appointment = appointmentService.getById(dto.appointmentId());
        if (appointment.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Appointment is already completed");
        }
        appointment.setStatus(Status.COMPLETED);
        Record record = recordMapper.toEntity(dto, appointment);
        Record saved = recordRepository.save(record);
        return HashId.of(saved.getId());
    }

    @Override
    public Record getById(HashId id) {
        return recordRepository.findById(id.value())
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    }
}
