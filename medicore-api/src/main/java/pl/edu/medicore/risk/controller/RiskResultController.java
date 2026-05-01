package pl.edu.medicore.risk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.risk.service.contract.RiskResultService;
import pl.edu.medicore.wrapper.ResponseWrapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/risks")
@Tag(name = "Risks", description = "Endpoints for managing estimated risk for selected diseases")
@RequiredArgsConstructor
public class RiskResultController {
    private final RiskResultService riskResultService;

    @Operation(summary = "Find latest estimated risks for selected diseases by patient id")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping("/{patientId}")
    public ResponseWrapper<List<RiskResultResponseDto>> getLatestRiskByPatientId(@PathVariable UUID patientId) {
        List<RiskResultResponseDto> risks = riskResultService.getLatestByPatientId(patientId);
        if (risks.isEmpty()) return ResponseWrapper.withStatus(HttpStatus.NO_CONTENT, risks);
        return ResponseWrapper.ok(riskResultService.getLatestByPatientId(patientId));
    }
}
