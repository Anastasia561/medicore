package pl.edu.medicore.profile.mapper;

import org.mapstruct.Mapper;
import pl.edu.medicore.address.mapper.AddressMapper;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.profile.dto.DoctorProfileResponseDto;
import pl.edu.medicore.profile.dto.PatientProfileResponseDto;
import pl.edu.medicore.profile.dto.ProfileResponseDto;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface ProfileMapper {

    ProfileResponseDto toDto(Person person);

    DoctorProfileResponseDto toDoctorDto(Doctor doctor);

    PatientProfileResponseDto toPatientDto(Patient patient);
}
