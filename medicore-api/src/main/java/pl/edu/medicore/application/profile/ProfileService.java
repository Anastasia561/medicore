package pl.edu.medicore.application.profile;

import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.profile.dto.ProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileUpdateDto;
import pl.edu.medicore.common.encryption.HashId;

public interface ProfileService {
    ProfileResponseDto getProfileById(HashId id, Role role);

    HashId updateProfile(ProfileUpdateDto dto, HashId id, Role role);
}
