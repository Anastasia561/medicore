package pl.edu.medicore.profile.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pl.edu.medicore.address.mapper.AddressMapper;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.profile.dto.DoctorProfileResponseDto;
import pl.edu.medicore.profile.dto.PatientProfileResponseDto;
import pl.edu.medicore.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.dto.ProfileUpdateDto;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface ProfileMapper {

    ProfileResponseDto toDto(Person person);

    DoctorProfileResponseDto toDoctorDto(Doctor doctor);

    PatientProfileResponseDto toPatientDto(Patient patient);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonFromDto(ProfileUpdateDto dto, @MappingTarget Person person);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePatientFromDto(PatientProfileUpdateDto dto, @MappingTarget Patient patient);
}
