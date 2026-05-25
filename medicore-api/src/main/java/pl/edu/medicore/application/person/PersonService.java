package pl.edu.medicore.application.person;


import pl.edu.medicore.application.auth.dto.PasswordResetDto;

import java.util.UUID;

public interface PersonService {
    Role getRoleByPublicId(UUID id);

    Person getByEmail(String email);

    Person getById(long id);

    void updatePassword(PasswordResetDto dto);

    boolean existsByEmail(String email);
}
