package pl.edu.medicore.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openapi")
@NoArgsConstructor
@Getter
@Setter
public class OpenApiProperties {

    private String resourceServerUrl;
    private String serverDescription;

}
