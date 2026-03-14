package pl.edu.medicore.person.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.auth.dto.PasswordResetDto;
import pl.edu.medicore.exception.UserNotVerifiedException;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.model.Status;
import pl.edu.medicore.person.repository.PersonRepository;

@Service
@RequiredArgsConstructor
class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Role getRoleById(Long id) {
        return personRepository.getRole(id).orElseThrow(
                () -> new EntityNotFoundException("Person not found"));
    }

    @Override
    public Person getByEmail(String email) {
        Person p = personRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("Person not found"));
        if (p.getStatus() == Status.UNVERIFIED)
            throw new UserNotVerifiedException("User not verified");
        return p;
    }

    @Override
    public Person getById(Long id) {
        return personRepository.findById(id).orElseThrow(
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
}
