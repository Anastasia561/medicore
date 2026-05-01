package pl.edu.medicore.doctor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.dto.DoctorInvitationRequestDto;
import pl.edu.medicore.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.statistics.dto.DoctorStatisticsDto;

import java.util.List;
import java.util.UUID;

public interface DoctorService {
    void checkExistsById(UUID doctorId);

    Doctor getByPublicId(UUID doctorId);
    Doctor getById(long doctorId);

    Page<DoctorResponseDto> getAll(DoctorFilterDto filter, Pageable pageable);

    List<DoctorStatisticsDto> getDoctorBySpecialization();

    long getTotalCount();

    void invite(DoctorInvitationRequestDto dto);

    long register(DoctorRegistrationDto dto);
}
