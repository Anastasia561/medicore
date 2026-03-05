package pl.edu.medicore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
@RequiredArgsConstructor
public class TestContainersConfig {
    private final PostgreSQLTestContainerProperties props;

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(props.getImage())
                .withUsername(props.getUsername())
                .withPassword(props.getPassword())
                .withDatabaseName(props.getDatabase());
    }
}
