package pl.edu.medicore.labresult.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ParameterConfig {
    private List<String> aliases;
    private List<String> sections;
}
