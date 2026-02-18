package pl.edu.medicore.consultation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.consultation.dto.ConsultationDto;
import pl.edu.medicore.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.consultation.service.ConsultationService;

import java.util.List;

@RestController
@RequestMapping("/consultations")
@RequiredArgsConstructor
public class ConsultationController {
    private final ConsultationService consultationService;

    @GetMapping
    public List<ConsultationDto> getAllForDoctor(@RequestParam Long doctorId) {
        return consultationService.findByDoctorId(doctorId);
    }

    @PostMapping
    public long create(@RequestBody @Valid ConsultationCreateDto dto) {
        return consultationService.create(dto);
    }

    @PutMapping("/{consultationId}")
    public long update(@RequestBody @Valid ConsultationUpdateDto dto, @PathVariable Long consultationId) {
        return consultationService.update(consultationId, dto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{consultationId}")
    public void delete(@PathVariable Long consultationId) {
        consultationService.delete(consultationId);
    }
}
