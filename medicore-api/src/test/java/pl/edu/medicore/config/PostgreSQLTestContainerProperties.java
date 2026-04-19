package pl.edu.medicore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "testcontainers.postgres")
public class PostgreSQLTestContainerProperties {
    private String image;
    private String username;
    private String password;
    private String database;
}
