package pl.edu.medicore.application.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.infrastructure.messaging.event.PatientUpdateEvent;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.person.Person;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.person.PersonService;
import pl.edu.medicore.application.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.application.profile.dto.ProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileUpdateDto;

@Service
@RequiredArgsConstructor
class ProfileServiceImpl implements ProfileService {
    private final PersonService personService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ProfileMapper profileMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    public ProfileResponseDto getProfileById(HashId id, Role role) {
        return switch (role) {
            case PATIENT -> profileMapper.toPatientDto(patientService.getById(id));
            case DOCTOR -> profileMapper.toDoctorDto(doctorService.getById(id));
            default -> profileMapper.toDto(personService.getById(id));
        };
    }

    @Override
    @Transactional
    public HashId updateProfile(ProfileUpdateDto dto, HashId id) {
        Person person = personService.getById(id);
        profileMapper.updatePersonFromDto(dto, person);
        return id;
    }

    @Override
    @Transactional
    public HashId updatePatientProfile(PatientProfileUpdateDto dto, HashId id) {
        Patient patient = patientService.getById(id);
        profileMapper.updatePatientFromDto(dto, patient);
        publisher.publishEvent(new PatientUpdateEvent(id));
        return id;
    }
}
