package pl.edu.medicore.doctor.service;

import pl.edu.medicore.doctor.model.Doctor;

public interface DoctorService {
    void checkExistsById(Long doctorId);
    Doctor getById(Long doctorId);
}
