package pl.edu.medicore.application.profile;

import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.application.profile.dto.ProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileUpdateDto;

import java.util.UUID;

public interface ProfileService {
    ProfileResponseDto getProfileById(long id, Role role);

    UUID updateProfile(ProfileUpdateDto dto, long id);

    UUID updatePatientProfile(PatientProfileUpdateDto dto, long id);
}
