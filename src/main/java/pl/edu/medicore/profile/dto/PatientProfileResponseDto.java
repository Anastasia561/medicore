package pl.edu.medicore.profile.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.address.dto.PatientAddressDto;

import java.time.LocalDate;

@Getter
@Setter
public class PatientProfileResponseDto extends ProfileResponseDto {
    private LocalDate birthDate;
    private String phoneNumber;
    private PatientAddressDto address;
}
