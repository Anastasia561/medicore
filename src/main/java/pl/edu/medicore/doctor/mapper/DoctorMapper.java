package pl.edu.medicore.doctor.mapper;

import org.mapstruct.Mapper;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.model.Doctor;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorResponseDto toDoctorResponseDto(Doctor doctor);
}
