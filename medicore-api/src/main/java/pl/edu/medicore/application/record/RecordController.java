package pl.edu.medicore.application.record;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.application.auth.CustomUserDetails;
import pl.edu.medicore.application.record.dto.RecordCreateDto;
import pl.edu.medicore.application.record.dto.RecordDto;
import pl.edu.medicore.application.record.dto.RecordFilterDto;
import pl.edu.medicore.application.record.dto.RecordPreviewDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.common.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/records")
@Tag(name = "Records", description = "Endpoints for managing medical records")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    @Operation(summary = "Find medical record by id")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping("/{id}")
    public ResponseWrapper<RecordDto> getById(@PathVariable HashId id) {
        return ResponseWrapper.ok(recordService.getById(id));
    }

    @Operation(summary = "Get page of medical records with filtering")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping
    public ResponseWrapper<Page<RecordPreviewDto>> getAllPageable(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid RecordFilterDto filter,
            Pageable pageable) {
        return ResponseWrapper.ok(recordService.getAllByPersonId(userDetails.getId(), userDetails.getRole(), filter, pageable));
    }

    @Operation(summary = "Create medical record after consultation")
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<HashId> create(@Valid @RequestBody RecordCreateDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, recordService.create(dto));
    }
}
