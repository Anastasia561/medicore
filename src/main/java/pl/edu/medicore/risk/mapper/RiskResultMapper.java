package pl.edu.medicore.risk.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.edu.medicore.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.risk.model.RiskResult;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface RiskResultMapper {

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "test.date", target = "testDate")
    @Mapping(source = "calculatedAt", target = "calculatedAt", qualifiedByName = "instantToLocalDate")
    RiskResultResponseDto toDto(RiskResult riskResult);

    @Named("instantToLocalDate")
    default LocalDate instantToLocalDate(Instant instant) {
        return instant == null
                ? null
                : instant.atZone(ZoneId.of("Europe/Warsaw")).toLocalDate();
    }
}
