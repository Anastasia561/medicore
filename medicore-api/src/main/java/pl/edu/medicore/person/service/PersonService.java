package pl.edu.medicore.person.service;


import pl.edu.medicore.auth.dto.PasswordResetDto;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.model.Role;

import java.util.UUID;

public interface PersonService {
    Role getRoleByPublicId(UUID id);

    Person getByEmail(String email);

    Person getById(long id);

    void updatePassword(PasswordResetDto dto);
}
