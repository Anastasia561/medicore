package pl.edu.medicore.labresult.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Parameter {
    RBC(new ParameterConfig(Arrays.asList("RBC", "RED"), Arrays.asList("HAEMATOLOGY", "CD4/CD8", "CBC"),
            "10^12/L", 1.0, List.of("10^12/L"))),
    HGB(new ParameterConfig(Arrays.asList("HEMOGLOBIN", "HAEMOGLOBIN"), Arrays.asList("HAEMATOLOGY", "CD4/CD8", "CBC"),
            "g/L", 10.0, List.of("g/dL"))),
    HCT(new ParameterConfig(Arrays.asList("HEMATOCRIT", "HCT"), Arrays.asList("HAEMATOLOGY", "CD4/CD8", "CBC"),
            "%", 100.0, List.of("L/L"))),
    CREATININE(new ParameterConfig(List.of("CREATININE"), Arrays.asList("COMPREHENSIVE", "BIOCHEMISTRY"),
            "mg/dL", 0.011312, List.of("umol/L"))),
    GLUCOSE(new ParameterConfig(List.of("GLUCOSE"), List.of("COMPREHENSIVE"), "mg/dL",
            1.0, List.of("mg/dL")));

    private final ParameterConfig config;

    Parameter(ParameterConfig config) {
        this.config = config;
    }
}
