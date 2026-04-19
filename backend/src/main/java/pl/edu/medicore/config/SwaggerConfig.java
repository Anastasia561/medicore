package pl.edu.medicore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.medicore.config.properties.OpenApiProperties;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final OpenApiProperties openApiProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "bearer-jwt";

        return new OpenAPI().info(new Info()
                        .title("Medicore API Documentation")
                        .version("1.0")
                        .description("Provides REST endpoints for managing doctors, patients and appointments"))
                .servers(List.of(new Server().url(openApiProperties.getResourceServerUrl())
                        .description(openApiProperties.getServerDescription())))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
