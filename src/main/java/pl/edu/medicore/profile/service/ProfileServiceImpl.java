package pl.edu.medicore.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.service.PersonService;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.mapper.ProfileMapper;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final PersonService personService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponseDto getProfileById(long id) {
        Role role = personService.getRoleById(id);
        return switch (role) {
            case PATIENT -> profileMapper.toPatientDto(patientService.getById(id));
            case DOCTOR -> profileMapper.toDoctorDto(doctorService.getById(id));
            default -> profileMapper.toDto(personService.getById(id));
        };
    }
}
