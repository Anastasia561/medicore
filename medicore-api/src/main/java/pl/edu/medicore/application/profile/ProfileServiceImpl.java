package pl.edu.medicore.application.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.infrastructure.messaging.event.PatientUpdateEvent;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.person.Person;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.person.PersonService;
import pl.edu.medicore.application.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.application.profile.dto.ProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileUpdateDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ProfileServiceImpl implements ProfileService {
    private final PersonService personService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ProfileMapper profileMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    public ProfileResponseDto getProfileById(long id, Role role) {
        return switch (role) {
            case PATIENT -> profileMapper.toPatientDto(patientService.getById(id));
            case DOCTOR -> profileMapper.toDoctorDto(doctorService.getById(id));
            default -> profileMapper.toDto(personService.getById(id));
        };
    }

    @Override
    @Transactional
    public UUID updateProfile(ProfileUpdateDto dto, long id) {
        Person person = personService.getById(id);
        profileMapper.updatePersonFromDto(dto, person);
        return person.getPublicId();
    }

    @Override
    @Transactional
    public UUID updatePatientProfile(PatientProfileUpdateDto dto, long id) {
        Patient patient = patientService.getById(id);
        profileMapper.updatePatientFromDto(dto, patient);
        publisher.publishEvent(new PatientUpdateEvent(patient.getPublicId()));
        return patient.getPublicId();
    }
}
