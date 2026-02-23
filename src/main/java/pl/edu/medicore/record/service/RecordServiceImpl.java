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
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordForDoctorPreviewDto;
import pl.edu.medicore.record.dto.RecordForPatientPreviewDto;
import pl.edu.medicore.record.mapper.RecordMapper;
import pl.edu.medicore.record.repository.RecordRepository;
import pl.edu.medicore.record.model.Record;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;
    private final AppointmentService appointmentService;

    @Override
    public RecordDto getByAppointmentId(Long id) {
        return recordRepository.findByAppointmentId(id)
                .map(recordMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    }

    @Override
    public Page<RecordForPatientPreviewDto> getAllByPatientId(Long patientId, Pageable pageable) {
        return recordRepository.findByPatientId(patientId, pageable)
                .map(recordMapper::toPatientPreviewDto);
    }

    @Override
    public Page<RecordForDoctorPreviewDto> getAllByDoctorAndPatientId(Long doctorId, Long patientId, Pageable pageable) {
        return recordRepository.findByDoctorAndPatientId(doctorId, patientId, pageable)
                .map(recordMapper::toDoctorPreviewDto);
    }

    @Override
    @Transactional
    public long create(RecordCreateDto dto) {
        Appointment appointment = appointmentService.getById(dto.appointmentId());
        if (appointment.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Appointment is already completed");
        }
        appointment.setStatus(Status.COMPLETED);
        Record record = recordMapper.toEntity(dto, appointment);
        return recordRepository.save(record).getId();
    }

    @Override
    public Record getById(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
    }
}
