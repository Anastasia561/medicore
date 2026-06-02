package pl.edu.medicore.application.person;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.medicore.application.auth.dto.PasswordResetDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.common.exception.UserNotVerifiedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
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
        person.setStatus(UserStatus.UNVERIFIED);
        when(personRepository.findByEmail("test")).thenReturn(Optional.of(person));
        UserNotVerifiedException ex = assertThrows(UserNotVerifiedException.class,
                () -> personService.getByEmail("test"));

        assertEquals("User not verified", ex.getMessage());
    }


    @Test
    void shouldReturnPerson_whenPersonExists() {
        Long rawId = 123L;
        HashId hashId = HashId.of(rawId);
        Person expectedPerson = new Person();

        when(personRepository.findById(rawId)).thenReturn(Optional.of(expectedPerson));

        Person actualPerson = personService.getById(hashId);

        assertNotNull(actualPerson);
        assertEquals(expectedPerson.getId(), actualPerson.getId());

        verify(personRepository, times(1)).findById(rawId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenPersonDoesNotExist() {
        Long rawId = 999L;
        HashId hashId = new HashId(rawId);

        when(personRepository.findById(rawId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            personService.getById(hashId);
        });

        assertEquals("Person not found", exception.getMessage());
        verify(personRepository, times(1)).findById(rawId);
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

    @Test
    void shouldReturnTrue_whenEmailExists() {
        String email = "test@example.com";
        when(personRepository.existsByEmail(email)).thenReturn(true);

        boolean result = personService.existsByEmail(email);
        assertTrue(result);
        verify(personRepository, times(1)).existsByEmail(email);
    }

    @Test
    void shouldReturnFalse_whenEmailDoesNotExist() {
        String email = "notfound@example.com";
        when(personRepository.existsByEmail(email)).thenReturn(false);

        boolean result = personService.existsByEmail(email);

        assertFalse(result);
        verify(personRepository, times(1)).existsByEmail(email);
    }
}
