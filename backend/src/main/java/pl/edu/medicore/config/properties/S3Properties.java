package pl.edu.medicore.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.aws.s3")
@Getter
@Setter
public class S3Properties {
    private String bucket;
    private String folderName;
    private String fileName;
    private Integer urlDurationMin;
}
