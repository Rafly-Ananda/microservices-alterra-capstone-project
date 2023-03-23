package com.alterra.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        String securityName = "bearerAuth";
        return new OpenAPI()
                .info(new Info().title("Order System - User API Documentation")
                        .description("API Documentation for User Service").version("1.0"))
                // Add security scheme to the OpenAPI specification to enable the Swagger UI to
                // send the Authorization header
                .addSecurityItem(new SecurityRequirement().addList(securityName))
                .components(new Components().addSecuritySchemes(securityName, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")));
    }
}
