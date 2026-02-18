package pl.edu.medicore.person.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.repository.PersonRepository;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public Role getRoleById(Long id) {
        return personRepository.getRole(id).orElseThrow(
                () -> new EntityNotFoundException("Person not found"));
    }
}
