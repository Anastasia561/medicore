package pl.edu.medicore.doctor.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.repository.DoctorRepository;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;

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
}
