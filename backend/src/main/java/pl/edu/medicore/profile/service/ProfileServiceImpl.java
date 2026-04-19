package pl.edu.medicore.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.infrastructure.messaging.event.PatientUpdateEvent;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.service.PersonService;
import pl.edu.medicore.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.dto.ProfileUpdateDto;
import pl.edu.medicore.profile.mapper.ProfileMapper;

@Service
@RequiredArgsConstructor
class ProfileServiceImpl implements ProfileService {
    private final PersonService personService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ProfileMapper profileMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    public ProfileResponseDto getProfileById(long id) {
        Role role = personService.getRoleById(id);
        return switch (role) {
            case PATIENT -> profileMapper.toPatientDto(patientService.getById(id));
            case DOCTOR -> profileMapper.toDoctorDto(doctorService.getById(id));
            default -> profileMapper.toDto(personService.getById(id));
        };
    }

    @Override
    @Transactional
    public long updateProfile(ProfileUpdateDto dto, long id) {
        Person person = personService.getById(id);
        profileMapper.updatePersonFromDto(dto, person);
        return id;
    }

    @Override
    @Transactional
    public long updatePatientProfile(PatientProfileUpdateDto dto, long id) {
        Patient patient = patientService.getById(id);
        profileMapper.updatePatientFromDto(dto, patient);
        publisher.publishEvent(new PatientUpdateEvent(id));
        return id;
    }
}
