package pl.edu.medicore.doctor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.statistics.dto.DoctorStatisticsDto;

import java.util.List;

public interface DoctorService {
    void checkExistsById(Long doctorId);

    Doctor getById(Long doctorId);

    Page<DoctorResponseDto> getAll(DoctorFilterDto filter, Pageable pageable);

    List<DoctorStatisticsDto> getDoctorBySpecialization();

    long getTotalCount();
}
