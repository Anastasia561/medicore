package pl.edu.medicore.statistics.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.statistics.dto.AdminStatisticsResponseDto;
import pl.edu.medicore.statistics.dto.ConsultationStatisticsDto;
import pl.edu.medicore.statistics.dto.DoctorStatisticsDto;
import pl.edu.medicore.statistics.dto.DoctorStatisticsResponseDto;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
    @Mock
    private PatientService patientService;
    @Mock
    private DoctorService doctorService;
    @Mock
    private AppointmentService appointmentService;
    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    void shouldReturnAdminStatistics_whenRecordsExist() {
        long patients = 100;
        long doctors = 25;
        long todayAppointments = 12;

        List<DoctorStatisticsDto> doctorStats = List.of(
                new DoctorStatisticsDto(Specialization.DERMATOLOGIST, 10),
                new DoctorStatisticsDto(Specialization.CARDIOLOGIST, 5)
        );

        List<ConsultationStatisticsDto> monthlyStats = List.of(
                new ConsultationStatisticsDto(1, Status.COMPLETED, 40),
                new ConsultationStatisticsDto(2, Status.CANCELLED, 60)
        );

        when(patientService.getTotalCount()).thenReturn(patients);
        when(doctorService.getTotalCount()).thenReturn(doctors);
        when(doctorService.getDoctorBySpecialization()).thenReturn(doctorStats);
        when(appointmentService.getTotalAppointmentsToday()).thenReturn(todayAppointments);
        when(appointmentService.getMonthlyStatistics()).thenReturn(monthlyStats);

        AdminStatisticsResponseDto result = statisticsService.getAdminStatistics();

        assertNotNull(result);
        assertEquals(patients, result.totalPatients());
        assertEquals(doctors, result.totalDoctors());
        assertEquals(todayAppointments, result.consultationsToday());
        assertEquals(monthlyStats, result.monthlyConsultations());
        assertEquals(doctorStats, result.doctorsBySpecialization());

        verify(patientService).getTotalCount();
        verify(doctorService).getTotalCount();
        verify(doctorService).getDoctorBySpecialization();
        verify(appointmentService).getTotalAppointmentsToday();
        verify(appointmentService).getMonthlyStatistics();
    }

    @Test
    void shouldReturnDoctorsStatistics_whenRecordsExist() {
        long patients = 100;
        long todayAppointments = 12;

        List<ConsultationStatisticsDto> monthlyStats = List.of(
                new ConsultationStatisticsDto(1, Status.COMPLETED, 40),
                new ConsultationStatisticsDto(2, Status.CANCELLED, 60)
        );

        when(appointmentService.getDistinctPatientsByDoctorId(2)).thenReturn(patients);
        when(appointmentService.getTotalAppointmentsTodayByDoctorId(2)).thenReturn(todayAppointments);
        when(appointmentService.getMonthlyStatisticsByDoctorId(2)).thenReturn(monthlyStats);

        DoctorStatisticsResponseDto result = statisticsService.getDoctorStatistics(2);

        assertNotNull(result);
        assertEquals(patients, result.totalPatients());
        assertEquals(todayAppointments, result.consultationsToday());
        assertEquals(monthlyStats, result.monthlyConsultations());

        verify(appointmentService).getDistinctPatientsByDoctorId(2);
        verify(appointmentService).getTotalAppointmentsTodayByDoctorId(2);
        verify(appointmentService).getMonthlyStatisticsByDoctorId(2);
    }
}
