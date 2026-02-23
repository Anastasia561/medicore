package pl.edu.medicore.prescription.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.prescription.service.PrescriptionService;
import pl.edu.medicore.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<Long> create(@Valid @RequestBody PrescriptionCreateDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, prescriptionService.create(dto));
    }
}
