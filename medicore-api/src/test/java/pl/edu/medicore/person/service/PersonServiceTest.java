package pl.edu.medicore.person.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.medicore.auth.dto.PasswordResetDto;
import pl.edu.medicore.exception.UserNotVerifiedException;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.model.Status;
import pl.edu.medicore.person.repository.PersonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    @Mock
    private PersonRepository personRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
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
    void shouldThrowEntityNotFoundException_whenPersonDoesNotExistByEmail() {
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

    @Test
    void shouldGetPersonById_whenInputIsValid() {
        Person person = new Person();
        when(personRepository.findById(2L)).thenReturn(Optional.of(person));

        assertEquals(person, personService.getById(2L));
        verify(personRepository).findById(2L);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenPersonDoesNotExistById() {
        when(personRepository.findById(2L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> personService.getById(2L));

        assertEquals("Person not found", ex.getMessage());
    }

    @Test
    void shouldUpdatePassword_whenInputIsValid() {
        Person person = new Person();
        person.setPassword("password");

        PasswordResetDto dto = new PasswordResetDto("token", "test@gmail.com",
                "pass1", "pass1");

        when(personRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(person));
        when(passwordEncoder.encode("pass1")).thenReturn("encodedPassword");

        personService.updatePassword(dto);

        assertEquals("encodedPassword", person.getPassword());
        verify(passwordEncoder).encode("pass1");
    }

    @Test
    void shouldThrowException_whenPasswordsDoNotMatch() {
        PasswordResetDto dto = new PasswordResetDto("token", "test@gmail.com",
                "pass1", "pass2");

        Person person = new Person();

        when(personRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(person));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> personService.updatePassword(dto));

        assertEquals("Passwords don't match", exception.getMessage());
        verifyNoInteractions(passwordEncoder);
    }
}
