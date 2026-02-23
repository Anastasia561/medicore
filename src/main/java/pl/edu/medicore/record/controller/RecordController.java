package pl.edu.medicore.record.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordForDoctorPreviewDto;
import pl.edu.medicore.record.dto.RecordForPatientPreviewDto;
import pl.edu.medicore.record.service.RecordService;
import pl.edu.medicore.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    @GetMapping("/appointment/{appointmentId}")
    public ResponseWrapper<RecordDto> getByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseWrapper.ok(recordService.getByAppointmentId(appointmentId));
    }

    @GetMapping("/patient/{patientId}/doctor/{doctorId}")
    public ResponseWrapper<Page<RecordForDoctorPreviewDto>> getAllForDoctorAndPatient(
            @PathVariable Long doctorId,
            @PathVariable Long patientId,
            Pageable pageable) {
        return ResponseWrapper.ok(recordService.getAllByDoctorAndPatientId(doctorId, patientId, pageable));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseWrapper<Page<RecordForPatientPreviewDto>> getAllForPatient(
            @PathVariable Long patientId,
            Pageable pageable) {
        return ResponseWrapper.ok(recordService.getAllByPatientId(patientId, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<Long> create(@Valid @RequestBody RecordCreateDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, recordService.create(dto));
    }
}
