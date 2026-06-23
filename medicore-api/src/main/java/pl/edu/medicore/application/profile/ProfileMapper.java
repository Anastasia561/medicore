package pl.edu.medicore.application.profile;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pl.edu.medicore.application.address.AddressMapper;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.person.Person;
import pl.edu.medicore.application.profile.dto.DoctorProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileUpdateDto;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface ProfileMapper {

    ProfileResponseDto toDto(Person person);

    DoctorProfileResponseDto toDoctorDto(Doctor doctor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonFromDto(ProfileUpdateDto dto, @MappingTarget Person person);
}
