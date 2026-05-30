package pl.edu.medicore.application.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.application.appointment.AppointmentService;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.statistics.dto.AdminStatisticsResponseDto;
import pl.edu.medicore.application.statistics.dto.ConsultationStatisticsDto;
import pl.edu.medicore.application.statistics.dto.DoctorStatisticsDto;
import pl.edu.medicore.application.statistics.dto.DoctorStatisticsResponseDto;
import pl.edu.medicore.common.encryption.HashId;

import java.util.List;

@Service
@RequiredArgsConstructor
class StatisticsServiceImpl implements StatisticsService {
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
    public DoctorStatisticsResponseDto getDoctorStatistics(HashId id) {
        long totalPatients = appointmentService.getDistinctPatientsByDoctorId(id);
        long todayAppointments = appointmentService.getTotalAppointmentsTodayByDoctorId(id);
        List<ConsultationStatisticsDto> monthlyConsultations = appointmentService.getMonthlyStatisticsByDoctorId(id);
        return new DoctorStatisticsResponseDto(totalPatients, todayAppointments, monthlyConsultations);
    }
}
