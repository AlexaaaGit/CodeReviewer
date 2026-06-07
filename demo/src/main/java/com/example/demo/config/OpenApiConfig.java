package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 * Swagger UI is available at: http://localhost:8080/swagger-ui.html
 * The SecurityScheme allows testing authenticated endpoints via the "Authorize" button.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "DevBoard API",
                version = "1.0",
                description = "REST API for the DevBoard code review platform. " +
                        "Manage products, comments, categories, and users."
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    // Configuration is done via annotations above.
    // No additional beans are needed — springdoc auto-scans controllers.
}
