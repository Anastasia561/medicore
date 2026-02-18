package pl.edu.medicore.appointment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.mapper.AppointmentMapper;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.appointment.repository.AppointmentRepository;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.service.PersonService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final PersonService personService;
    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public Page<AppointmentInfoDto> getAppointmentsInRange(Long id, LocalDate start, LocalDate end, Pageable pageable) {
        Role role = personService.getRoleById(id);
        if (role == Role.PATIENT) {
            return appointmentRepository.findByPatientIdAndDateBetween(id, start, end, pageable)
                    .map(appointmentMapper::toPatientDto);
        } else if (role == Role.DOCTOR) {
            return appointmentRepository.findByDoctorIdAndDateBetween(id, start, end, pageable)
                    .map(appointmentMapper::toDoctorDto);
        }
        throw new IllegalArgumentException("Invalid role");
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Status status) {
        Appointment appointment = getById(id);
        appointment.setStatus(status);
    }

//    @Override
//    @Transactional
//    public long create(Long patientId, Long doctorId, LocalDate date, LocalTime time) {
//        return 0;
//    }
//
//    @Override
//    public List<LocalTime> getAvailableTimes(Long doctorId, LocalDate date) {
//        doctorService.getById(doctorId);
//    }

    private Appointment getById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Appointment not found"));
    }
}
