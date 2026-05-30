package pl.edu.medicore.application.doctor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.application.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.application.doctor.dto.DoctorInvitationRequestDto;
import pl.edu.medicore.application.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.application.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.application.statistics.dto.DoctorStatisticsDto;
import pl.edu.medicore.common.encryption.HashId;

import java.util.List;

public interface DoctorService {
    void checkExistsById(HashId doctorId);

    Doctor getById(HashId doctorId);

    Page<DoctorResponseDto> getAll(DoctorFilterDto filter, Pageable pageable);

    List<DoctorStatisticsDto> getDoctorBySpecialization();

    long getTotalCount();

    void invite(DoctorInvitationRequestDto dto);

    long register(DoctorRegistrationDto dto);
}
