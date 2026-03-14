package pl.edu.medicore.profile.service;

import pl.edu.medicore.profile.dto.ProfileResponseDto;

public interface ProfileService {
    ProfileResponseDto getProfileById(long id);
}
