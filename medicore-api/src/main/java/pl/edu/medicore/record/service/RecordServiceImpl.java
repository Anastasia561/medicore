package pl.edu.medicore.record.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordFilterDto;
import pl.edu.medicore.record.dto.RecordPreviewDto;
import pl.edu.medicore.record.mapper.RecordMapper;
import pl.edu.medicore.record.repository.RecordRepository;
import pl.edu.medicore.record.model.Record;
import pl.edu.medicore.record.repository.specification.RecordSpecification;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;
    private final AppointmentService appointmentService;

    @Override
    public RecordDto getByAppointmentId(UUID id) {
        return recordRepository.findByAppointmentPublicId(id)
                .map(recordMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    }

    @Override
    public Page<RecordPreviewDto> getAllById(long id, Role role, RecordFilterDto filter, Pageable pageable) {
        if (filter.startDate() != null && filter.endDate() != null
                && filter.startDate().isAfter(filter.endDate()))
            throw new IllegalArgumentException("Start date should be before end date");

        Page<Record> all = recordRepository.findAll(RecordSpecification
                .withFilter(id, role, filter), pageable);

        return role == Role.DOCTOR ? all.map(recordMapper::toDoctorPreviewDto)
                : all.map(recordMapper::toPatientPreviewDto);
    }

    @Override
    @Transactional
    public UUID create(RecordCreateDto dto) {
        Appointment appointment = appointmentService.getByPublicId(dto.appointmentId());
        if (appointment.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Appointment is already completed");
        }
        appointment.setStatus(Status.COMPLETED);
        Record record = recordMapper.toEntity(dto, appointment);
        return recordRepository.save(record).getPublicId();
    }

    @Override
    public Record getByPublicId(UUID id) {
        return recordRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    }
}
