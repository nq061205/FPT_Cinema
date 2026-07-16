package com.group6.mvc.fpt_cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI fptCinemaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FPT Cinema API")
                        .description("API documentation for the FPT Cinema booking system")
                        .version("v1")
                        .contact(new Contact().name("Group 6")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SECURITY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SECURITY_SCHEME, new SecurityScheme()
                                .name(BEARER_SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
