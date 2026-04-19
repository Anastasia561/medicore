package pl.edu.medicore.profile.service;

import pl.edu.medicore.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.dto.ProfileUpdateDto;

public interface ProfileService {
    ProfileResponseDto getProfileById(long id);

    long updateProfile(ProfileUpdateDto dto, long id);

    long updatePatientProfile(PatientProfileUpdateDto dto, long id);
}
