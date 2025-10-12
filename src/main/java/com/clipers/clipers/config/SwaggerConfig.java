package com.clipers.clipers.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

import java.util.Arrays;

/**
 * Configuration class for Swagger/OpenAPI documentation
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clippers AI + ATS Integration API")
                        .version("1.0.0")
                        .description("API for integrating AI-powered candidate matching with ATS systems")
                        .termsOfService("https://clipers.com/terms")
                        .contact(new Contact()
                                .name("Clippers API Team")
                                .email("api@clipers.com")
                                .url("https://clipers.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("Local development server"),
                        new Server().url("https://api.clipers.com").description("Production server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }

    @Bean
    public GroupedOpenApi integrationOpenApi() {
        return GroupedOpenApi.builder()
                .group("integration")
                .pathsToMatch("/integration/**")
                .build();
    }
}