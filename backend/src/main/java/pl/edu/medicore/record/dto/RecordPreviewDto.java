package pl.edu.medicore.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class RecordPreviewDto {
    private LocalDate date;
}
