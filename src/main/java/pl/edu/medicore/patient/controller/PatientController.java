package pl.edu.medicore.patient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public ResponseWrapper<Page<PatientResponseDto>> getAllPageable(Pageable pageable) {
        return ResponseWrapper.ok(patientService.findAll(pageable));
    }
}
