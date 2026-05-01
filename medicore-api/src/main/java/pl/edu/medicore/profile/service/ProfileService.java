package pl.edu.medicore.profile.service;

import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.dto.ProfileUpdateDto;

import java.util.UUID;

public interface ProfileService {
    ProfileResponseDto getProfileById(long id, Role role);

    UUID updateProfile(ProfileUpdateDto dto, long id);

    UUID updatePatientProfile(PatientProfileUpdateDto dto, long id);
}
