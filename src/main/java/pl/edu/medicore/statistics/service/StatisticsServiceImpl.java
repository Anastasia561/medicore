package pl.edu.medicore.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.statistics.dto.AdminStatisticsResponseDto;
import pl.edu.medicore.statistics.dto.ConsultationStatisticsDto;
import pl.edu.medicore.statistics.dto.DoctorStatisticsDto;
import pl.edu.medicore.statistics.dto.DoctorStatisticsResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    @Override
    public AdminStatisticsResponseDto getAdminStatistics() {
        long totalPatients = patientService.getTotalCount();
        long totalDoctors = doctorService.getTotalCount();
        List<DoctorStatisticsDto> doctorsBySpecialization = doctorService.getDoctorBySpecialization();
        long todayAppointments = appointmentService.getTotalAppointmentsToday();
        List<ConsultationStatisticsDto> monthlyConsultations = appointmentService.getMonthlyStatistics();

        return new AdminStatisticsResponseDto(totalPatients, totalDoctors, todayAppointments, monthlyConsultations,
                doctorsBySpecialization);
    }

    @Override
    public DoctorStatisticsResponseDto getDoctorStatistics(long id) {
        long totalPatients = appointmentService.getDistinctPatientsByDoctorId(id);
        long todayAppointments = appointmentService.getTotalAppointmentsTodayByDoctorId(id);
        List<ConsultationStatisticsDto> monthlyConsultations = appointmentService.getMonthlyStatisticsByDoctorId(id);
        return new DoctorStatisticsResponseDto(totalPatients, todayAppointments, monthlyConsultations);
    }
}
