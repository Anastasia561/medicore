package pl.edu.medicore.application.prescription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.application.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.common.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/prescriptions")
@Tag(name = "Prescriptions", description = "Endpoints for managing medical prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @Operation(summary = "Create prescription")
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<HashId> create(@Valid @RequestBody PrescriptionCreateDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, prescriptionService.create(dto));
    }
}
