package pl.edu.medicore.application.person;

import pl.edu.medicore.application.auth.dto.PasswordResetDto;
import pl.edu.medicore.common.encryption.HashId;

public interface PersonService {

    Person getByEmail(String email);

    Person getById(HashId id);

    void updatePassword(PasswordResetDto dto);

    boolean existsByEmail(String email);
}
