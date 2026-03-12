package pl.edu.medicore.person.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.medicore.exception.UserNotVerifiedException;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.model.Status;
import pl.edu.medicore.person.repository.PersonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    @Mock
    private PersonRepository personRepository;
    @InjectMocks
    private PersonServiceImpl personService;

    @Test
    void shouldGetRoleByPersonId_whenInputIsValid() {
        when(personRepository.getRole(1L)).thenReturn(Optional.of(Role.ADMIN));

        assertEquals(Role.ADMIN, personService.getRoleById(1L));
        verify(personRepository).getRole(1L);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenRoleNotFoundById() {
        when(personRepository.getRole(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> personService.getRoleById(1L));

        assertEquals("Person not found", ex.getMessage());
    }

    @Test
    void shouldGetPersonByEmail_whenInputIsValid() {
        Person person = new Person();
        when(personRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(person));

        assertEquals(person, personService.getByEmail("test@gmail.com"));
        verify(personRepository).findByEmail("test@gmail.com");
    }

    @Test
    void shouldThrowEntityNotFoundException_whenPersonDoesNotExist() {
        when(personRepository.findByEmail("test")).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> personService.getByEmail("test"));

        assertEquals("Person not found", ex.getMessage());
    }

    @Test
    void shouldThrowUserNotVerifiedException_whenPersonIsNotVerified() {
        Person person = new Person();
        person.setStatus(Status.UNVERIFIED);
        when(personRepository.findByEmail("test")).thenReturn(Optional.of(person));
        UserNotVerifiedException ex = assertThrows(UserNotVerifiedException.class,
                () -> personService.getByEmail("test"));

        assertEquals("User not verified", ex.getMessage());
    }
}
