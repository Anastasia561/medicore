package pl.edu.medicore.labresult.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Parameter {
    RBC(new ParameterConfig(Arrays.asList("RBC", "RED"), Arrays.asList("HAEMATOLOGY", "CD4/CD8", "CBC"))),
    HGB(new ParameterConfig(Arrays.asList("HEMOGLOBIN", "HAEMOGLOBIN"), Arrays.asList("HAEMATOLOGY", "CD4/CD8", "CBC"))),
    HCT(new ParameterConfig(Arrays.asList("HEMATOCRIT", "HCT"), Arrays.asList("HAEMATOLOGY", "CD4/CD8", "CBC"))),
    CREATININE(new ParameterConfig(List.of("CREATININE"), Arrays.asList("COMPREHENSIVE", "BIOCHEMISTRY"))),
    GLUCOSE(new ParameterConfig(List.of("GLUCOSE"), List.of("COMPREHENSIVE")));

    private final ParameterConfig config;

    Parameter(ParameterConfig config) {
        this.config = config;
    }
}
