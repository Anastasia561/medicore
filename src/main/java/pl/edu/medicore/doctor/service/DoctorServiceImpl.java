package pl.edu.medicore.doctor.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.mapper.DoctorMapper;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.repository.DoctorRepository;
import pl.edu.medicore.doctor.repository.specification.DoctorSpecification;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public void checkExistsById(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new EntityNotFoundException("Doctor not found");
        }
    }

    @Override
    public Doctor getById(Long id) {
        return doctorRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Doctor not found"));
    }

    @Override
    public Page<DoctorResponseDto> getAll(DoctorFilterDto filter, Pageable pageable) {
        Page<Doctor> all = (filter == null) ? doctorRepository.findAll(pageable)
                : doctorRepository.findAll(DoctorSpecification.search(filter), pageable);

        return all.map(doctorMapper::toDoctorResponseDto);
    }
}
