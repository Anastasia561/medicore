package pl.edu.medicore.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalTime;


@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.doctor.working-hours")
public class ConsultationProperties {
    private LocalTime start;
    private LocalTime end;
}
