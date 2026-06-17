package pl.edu.medicore.application.record.dto;

import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;

@Getter
@Setter
public abstract class RecordPreviewDto {
    private HashId id;
    private LocalDate date;
}
