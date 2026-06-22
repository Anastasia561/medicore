package pl.edu.medicore.application.profile.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.address.dto.AddressDto;
import pl.edu.medicore.application.person.Gender;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileResponseDto {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected Gender gender;
    protected LocalDate birthDate;
    protected String phoneNumber;
    protected AddressDto address;
}
