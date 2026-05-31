package pl.edu.medicore.application.person;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.auth.dto.PasswordResetDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.common.exception.UserNotVerifiedException;

@Service
@RequiredArgsConstructor
class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Person getByEmail(String email) {
        Person p = personRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("Person not found"));
        if (p.getStatus() == UserStatus.UNVERIFIED)
            throw new UserNotVerifiedException("User not verified");
        return p;
    }

    @Override
    public Person getById(HashId id) {
        return personRepository.findById(id.value()).orElseThrow(
                () -> new EntityNotFoundException("Person not found"));
    }

    @Override
    @Transactional
    public void updatePassword(PasswordResetDto dto) {
        Person person = getByEmail(dto.email());

        if (!dto.password().equals(dto.repeatPassword()))
            throw new IllegalArgumentException("Passwords don't match");

        person.setPassword(passwordEncoder.encode(dto.password()));
    }

    @Override
    public boolean existsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }
}
